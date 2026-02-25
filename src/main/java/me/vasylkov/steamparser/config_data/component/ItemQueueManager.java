package me.vasylkov.steamparser.config_data.component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.vasylkov.steamparser.properties.DataProperties;
import me.vasylkov.steamparser.config_data.model.Item;
import me.vasylkov.steamparser.config_data.model.PricedItem;
import me.vasylkov.steamparser.price_api.component.ItemPriceFetcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@Component
@Data
public class ItemQueueManager {
    private final UrlGenerator urlGenerator;
    private final DataProperties dataProperties;
    @Qualifier("itemPriceFetcher")
    private final ItemPriceFetcher steamItemPriceFetcher;
    private final ConcurrentLinkedDeque<PricedItem> itemsQueue;

    public ItemQueueManager(UrlGenerator urlGenerator, DataProperties dataProperties, ItemPriceFetcher steamItemPriceFetcher) {
        this.urlGenerator = urlGenerator;
        this.dataProperties = dataProperties;
        this.steamItemPriceFetcher = steamItemPriceFetcher;
        this.itemsQueue = new ConcurrentLinkedDeque<>();
    }

    public synchronized void refreshItemsQueue() {
        itemsQueue.clear();

        log.info("Обновляем цены и формируем новую очередь предметов…");

        for (Item original : dataProperties.getItems()) {
            double avgItemPrice = steamItemPriceFetcher.fetchItemAveragePrice(urlGenerator.generatePriceOverviewApiUrl(original.getHashName()));

            if (avgItemPrice > 0.0) {
                PricedItem pricedItem = new PricedItem(original.getHashName(), original.getMaximalPage(), original.getStickersModule(), avgItemPrice, true);
                itemsQueue.add(pricedItem);
            } else {
                log.warn("Не удалось получить цену для {} – пропускаем на текущую сессию",
                        original.getHashName());
            }
        }
        log.info("Очередь сформирована ({} предметов)", itemsQueue.size());
    }

    private synchronized PricedItem getAndBlockFirstAvailableItem() {
        for (PricedItem item : itemsQueue) {
            if (Boolean.TRUE.equals(item.getAvailable())) {
                item.setAvailable(false);
                return item;
            }
        }
        return null;
    }

    public synchronized PricedItem getAvailableItem() {
        PricedItem currentAvailable = getAndBlockFirstAvailableItem();
        if (currentAvailable == null) {
            log.info("Предметов для потока {} нет, поток не будет продолжать работу", Thread.currentThread().getId());
        }
        return currentAvailable;
    }

    public synchronized void addItem(PricedItem item) {
        itemsQueue.add(item);
    }

    public synchronized void moveItemToLastAndUnblock(PricedItem item) {
        item.setAvailable(true);
        itemsQueue.remove(item);
        itemsQueue.addLast(item);
    }
}
