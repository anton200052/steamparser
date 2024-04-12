package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.httpclient.object.Item;
import me.vasylkov.steamparser.parsing.component.ItemsStorage;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParsingService
{
    private final Logger logger;
    private final ItemsStorage linkStorage;
    private final ChromeOptions options;

    public void parsePage(Item item)
    {
        logger.info("Начинаем парсинг предмета " + item.getName());
    }

}
