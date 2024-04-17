package me.vasylkov.steamparser.data.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.configuration.DataProperties;
import me.vasylkov.steamparser.data.entity.ItemData;
import me.vasylkov.steamparser.data.entity.StickersModule;
import me.vasylkov.steamparser.httpclient.service.SteamItemPriceGetter;
import me.vasylkov.steamparser.data.component.ItemDataQueue;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemDataQueueFiller
{
    private final ItemDataUrlGenerator itemDataUrlGenerator;
    private final ItemDataQueue itemDataQueue;
    private final SteamItemPriceGetter steamItemPriceGetter;
    private final DataProperties dataProperties;
    private final Logger logger;

    public void fillDataQueue()
    {
        for (ItemData itemData : dataProperties.getItems())
        {
            String hashName = itemData.getHashName();
            String listingsUrl = itemDataUrlGenerator.generateListingsUrl(hashName);
            String apiUrl = itemDataUrlGenerator.generatePriceOverviewApiUrl(hashName);
            double medianPrice = steamItemPriceGetter.getItemMedianPrice(apiUrl);
            itemData.setListingsUrl(listingsUrl);
            itemData.setApiUrl(apiUrl);
            itemData.setMedianPrice(medianPrice);

            if (itemData.isValid())
            {
                itemDataQueue.addToQueue(itemData);
            }
            else
            {
                logger.warn("Ошибка при загрузке предмета {}, проверьте конфигурацию. Парсинг этого предмета НЕ будет запущен", hashName);
            }
        }
    }
}
