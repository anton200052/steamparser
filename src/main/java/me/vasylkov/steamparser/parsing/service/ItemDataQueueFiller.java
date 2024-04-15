package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.httpclient.entity.ItemToParseData;
import me.vasylkov.steamparser.httpclient.service.SteamItemPriceGetter;
import me.vasylkov.steamparser.parsing.component.ItemDataQueue;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ItemDataQueueFiller
{

    private final Logger logger;
    private final ItemDataUrlGenerator itemDataUrlGenerator;
    private final ItemDataQueue itemDataQueue;
    private final SteamItemPriceGetter steamItemPriceGetter;

    public void fillQueueFromFile(String filePath)
    {
        try
        {
            Files.lines(Path.of(filePath), StandardCharsets.UTF_8).forEach(hashName ->
            {
                String listingsUrl = itemDataUrlGenerator.generateListingsUrl(hashName);
                String apiUrl = itemDataUrlGenerator.generatePriceOverviewApiUrl(hashName);
                double medianPrice = steamItemPriceGetter.getItemMedianPrice(apiUrl);

                if (medianPrice > 0.0)
                {
                    ItemToParseData itemToParseData = new ItemToParseData(medianPrice, hashName, listingsUrl);
                    itemDataQueue.addToQueue(itemToParseData);
                }
            });
        }
        catch (IOException e)
        {
            logger.error("Ошибка прочтения из файла: " + filePath, e);
        }
    }
}
