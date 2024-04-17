package me.vasylkov.steamparser.data.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.service.ItemDataQueueFiller;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer
{
    private final ItemDataQueueFiller itemDataQueueFiller;
    private final Logger logger;

    public void init()
    {
        logger.info("Загружаем данные в программу");
        itemDataQueueFiller.fillDataQueue();
    }
}
