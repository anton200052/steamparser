package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.httpclient.entity.SteamItem;
import me.vasylkov.steamparser.httpclient.service.ItemFetcher;
import me.vasylkov.steamparser.parsing.component.ItemsStorage;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ItemQueueFiller
{

    private final Logger logger;
    private final ItemUrlGenerator itemUrlGenerator;
    private final ItemsStorage itemsStorage;
    private final ItemFetcher itemFetcher;

    public void fillQueueFromFile(String filePath)
    {
        try
        {
            Files.lines(Path.of(filePath), StandardCharsets.UTF_8).forEach(name ->
            {
                String marketUrl = itemUrlGenerator.generateListingsUrl(name);
                String apiUrl = itemUrlGenerator.generatePriceLink(name);
                SteamItem item = itemFetcher.fetchItem(apiUrl, marketUrl, name);
                if (item != null)
                {
                    itemsStorage.addToQueue(item);
                }
            });
        }
        catch (IOException e)
        {
            logger.error("Ошибка прочтения из файла: " + filePath, e);
        }
    }
}
