package me.vasylkov.steamparser.spring.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.parsing.service.ItemDataQueueFiller;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer
{
    private final ItemDataQueueFiller itemDataQueueFiller;
    private final ParsingProperties parsingProperties;
    private final Logger logger;

    public void init()
    {
        logger.info("Загружаем данные в программу");
        String filePath = parsingProperties.getItemsFilePath();
        itemDataQueueFiller.fillQueueFromFile(filePath);
    }
}
