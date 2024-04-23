package me.vasylkov.steamparser.data.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.component.ItemQueue;
import me.vasylkov.steamparser.data.component.ItemUrlGenerator;
import me.vasylkov.steamparser.data.configuration.DataProperties;
import me.vasylkov.steamparser.data.entity.SteamItem;
import me.vasylkov.steamparser.price_api.service.SteamItemPriceFetcher;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SteamItemQueueFiller implements ItemQueueFiller
{
    private final ItemUrlGenerator itemUrlGenerator;
    private final ItemQueue<SteamItem> steamItemsQueue;
    private final SteamItemPriceFetcher steamItemPriceFetcher;
    private final DataProperties dataProperties;
    private final Logger logger;

    @Override
    public void fillItemQueue()
    {
        for (SteamItem steamItem : dataProperties.getSteamItems())
        {
            String hashName = steamItem.getHashName();
            double averagePrice = steamItemPriceFetcher.fetchItemAveragePrice(itemUrlGenerator.generatePriceOverviewApiUrl(hashName));
            String listingsUrl = itemUrlGenerator.generateListingsUrl(hashName);

            steamItem.setAveragePrice(averagePrice);
            steamItem.setListingsUrl(listingsUrl);

            if (steamItem.isValid())
            {
                steamItemsQueue.addToQueue(steamItem);
            }
            else
            {
                logger.warn("Ошибка при загрузке предмета {}, проверьте конфигурацию. Парсинг этого предмета НЕ будет запущен", hashName);
            }
        }
    }
}
