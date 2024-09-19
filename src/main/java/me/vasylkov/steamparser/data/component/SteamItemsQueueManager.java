package me.vasylkov.steamparser.data.component;

import lombok.Data;
import me.vasylkov.steamparser.data.configuration.DataProperties;
import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.data.entity.SteamItem;
import me.vasylkov.steamparser.price_api.component.ItemPriceFetcher;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@Data
public class SteamItemsQueueManager implements ItemQueueManager
{
    private final SteamItemUrlGenerator steamItemUrlGenerator;
    private final DataProperties dataProperties;
    private final Logger logger;
    @Qualifier("steamItemPriceFetcher")
    private final ItemPriceFetcher steamItemPriceFetcher;

    public SteamItemsQueueManager(SteamItemUrlGenerator steamItemUrlGenerator, DataProperties dataProperties, Logger logger, ItemPriceFetcher steamItemPriceFetcher)
    {
        this.steamItemUrlGenerator = steamItemUrlGenerator;
        this.dataProperties = dataProperties;
        this.logger = logger;
        this.steamItemPriceFetcher = steamItemPriceFetcher;
    }

    private synchronized SteamItem getAndBlockFirstAvailableItem()
    {
        for (SteamItem item : dataProperties.getSteamItems())
        {
            if (item.getAvailable())
            {
                item.setAvailable(false);
                return item;
            }
        }
        return null;
    }

    @Override
    public synchronized Item getAvailableOrLastItem(Item lastAvailable, boolean isParsingCycled)
    {
        Item currentAvailable = getAndBlockFirstAvailableItem();
        if (currentAvailable == null)
        {
            if (lastAvailable == null || !isParsingCycled)
            {
                logger.info("Предметов для потока {} нет, поток не будет продолжать работу", Thread.currentThread().getId());
                return null;
            }
            currentAvailable = lastAvailable;
        }
        return currentAvailable;
    }

    @Override
    public synchronized void addItem(Item item)
    {
        dataProperties.getSteamItems().add((SteamItem) item);
    }

    @Override
    public synchronized void moveItemToLastAndUnblock(Item item)
    {
        item.setAvailable(true);
        dataProperties.getSteamItems().remove((SteamItem) item);
        dataProperties.getSteamItems().addLast((SteamItem) item);
    }

    public void updatePricesInQueue()
    {
        logger.info("Обновляем цены на предметы");
        ConcurrentLinkedDeque<SteamItem> steamItems = dataProperties.getSteamItems();
        for (SteamItem steamItem : steamItems)
        {
            String hashName = steamItem.getHashName();
            double averagePrice = steamItemPriceFetcher.fetchItemAveragePrice(steamItemUrlGenerator.generatePriceOverviewApiUrl(hashName));

            if (averagePrice > 0.0)
            {
                steamItem.setAveragePrice(averagePrice);
            }

            if (!steamItem.isValid())
            {
                steamItems.remove(steamItem);
                logger.warn("Ошибка при загрузке предмета {}. Парсинг этого предмета НЕ будет запущен", hashName);
            }
        }
    }
}
