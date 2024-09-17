package me.vasylkov.steamparser.data.component;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.configuration.DataProperties;
import me.vasylkov.steamparser.data.entity.SteamItem;
import me.vasylkov.steamparser.price_api.component.ItemPriceFetcher;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@Data
public class SteamItemsQueueManager implements ItemQueueManager<SteamItem>
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

    @Override
    public synchronized SteamItem getAndBlockFirstAvailableItem()
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
    public synchronized void addItem(SteamItem item)
    {
        dataProperties.getSteamItems().add(item);
    }

    @Override
    public synchronized void moveItemToLastAndUnblock(SteamItem item)
    {
        item.setAvailable(true);
        dataProperties.getSteamItems().remove(item);
        dataProperties.getSteamItems().addLast(item);
    }

    public void updateItemsPrices()
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
