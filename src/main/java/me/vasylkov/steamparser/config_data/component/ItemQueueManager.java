package me.vasylkov.steamparser.config_data.component;

import lombok.Data;
import me.vasylkov.steamparser.config_data.configuration.DataProperties;
import me.vasylkov.steamparser.config_data.model.Item;
import me.vasylkov.steamparser.config_data.model.PricedItem;
import me.vasylkov.steamparser.price_api.component.ItemPriceFetcher;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@Data
public class ItemQueueManager
{
    private final UrlGenerator urlGenerator;
    private final DataProperties dataProperties;
    private final Logger logger;
    @Qualifier("itemPriceFetcher")
    private final ItemPriceFetcher steamItemPriceFetcher;
    private final ConcurrentLinkedDeque<PricedItem> itemsQueue;

    public ItemQueueManager(UrlGenerator urlGenerator, DataProperties dataProperties, Logger logger, ItemPriceFetcher steamItemPriceFetcher)
    {
        this.urlGenerator = urlGenerator;
        this.dataProperties = dataProperties;
        this.logger = logger;
        this.steamItemPriceFetcher = steamItemPriceFetcher;
        this.itemsQueue = new ConcurrentLinkedDeque<>();
    }

    public synchronized void refreshItemsQueue() {
        itemsQueue.clear();

        logger.info("Обновляем цены и формируем новую очередь предметов…");

        for (Item original : dataProperties.getItems()) {
            double avgItemPrice = steamItemPriceFetcher.fetchItemAveragePrice(
                    urlGenerator.generatePriceOverviewApiUrl(original.getHashName()));

            if (avgItemPrice > 0.0) {
                PricedItem pricedItem = new PricedItem(original.getHashName(), original.getMaximalPage(), original.getStickersModule(), avgItemPrice, true);
                itemsQueue.add(pricedItem);
            } else {
                logger.warn("Не удалось получить цену для {} – пропускаем на текущую сессию",
                            original.getHashName());
            }
        }
        logger.info("Очередь сформирована ({} предметов)", itemsQueue.size());
    }

    private synchronized PricedItem getAndBlockFirstAvailableItem()
    {
        for (PricedItem item : itemsQueue)
        {
            if (Boolean.TRUE.equals(item.getAvailable()))
            {
                item.setAvailable(false);
                return item;
            }
        }
        return null;
    }

    public synchronized PricedItem getAvailableOrLastItem(PricedItem lastAvailable, boolean isParsingCycled)
    {
        PricedItem currentAvailable = getAndBlockFirstAvailableItem();
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

    public synchronized void addItem(PricedItem item)
    {
        itemsQueue.add(item);
    }

    public synchronized void moveItemToLastAndUnblock(PricedItem item)
    {
        item.setAvailable(true);
        itemsQueue.remove(item);
        itemsQueue.addLast(item);
    }
}
