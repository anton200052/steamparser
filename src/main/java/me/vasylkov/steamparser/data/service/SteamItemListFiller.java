package me.vasylkov.steamparser.data.service;

import jakarta.annotation.PostConstruct;
import me.vasylkov.steamparser.data.component.ItemQueueManager;
import me.vasylkov.steamparser.data.component.SteamItemUrlGenerator;
import me.vasylkov.steamparser.data.configuration.DataProperties;
import me.vasylkov.steamparser.data.entity.SteamItem;
import me.vasylkov.steamparser.price_api.service.ItemPriceFetcher;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SteamItemListFiller implements ItemListFiller
{
    private final SteamItemUrlGenerator steamItemUrlGenerator;
    private final ItemQueueManager<SteamItem> steamItemList;
    @Qualifier("steamItemPriceFetcher")
    private final ItemPriceFetcher steamItemPriceFetcher;
    private final DataProperties dataProperties;
    private final Logger logger;

    public SteamItemListFiller(SteamItemUrlGenerator steamItemUrlGenerator, ItemQueueManager<SteamItem> steamItemsQueue, ItemPriceFetcher steamItemPriceFetcher, DataProperties dataProperties, Logger logger)
    {
        this.steamItemUrlGenerator = steamItemUrlGenerator;
        this.steamItemList = steamItemsQueue;
        this.steamItemPriceFetcher = steamItemPriceFetcher;
        this.dataProperties = dataProperties;
        this.logger = logger;
    }

    @Override
    @PostConstruct
    public void fillItemList()
    {
        logger.info("Загружаем предметы в список для парсинга");
        for (SteamItem steamItem : dataProperties.getSteamItems())
        {
            String hashName = steamItem.getHashName();
            double averagePrice = steamItemPriceFetcher.fetchItemAveragePrice(steamItemUrlGenerator.generatePriceOverviewApiUrl(hashName));
            String listingsUrl = steamItemUrlGenerator.generateListingsUrl(hashName);

            if (averagePrice > 0.0)
            {
                steamItem.setAveragePrice(averagePrice);
            }
            steamItem.setListingsUrl(listingsUrl);

            if (steamItem.isValid())
            {
                steamItemList.addItem(steamItem);
            }
            else
            {
                logger.warn("Ошибка при загрузке предмета {}. Парсинг этого предмета НЕ будет запущен", hashName);
            }
        }
    }
}
