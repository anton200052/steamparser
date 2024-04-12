package me.vasylkov.steamparser.spring.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.selenium.configuration.SeleniumProperties;
import me.vasylkov.steamparser.parsing.service.ItemQueueFiller;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer
{
    private final ItemQueueFiller itemQueueFiller;
    private final ParsingProperties parsingProperties;
    private final Logger logger;

    public void init()
    {
        logger.info("Загружаем данные в программу");
        String filePath = parsingProperties.getItemsFilePath();
        itemQueueFiller.fillQueueFromFile(filePath);
    }
}
