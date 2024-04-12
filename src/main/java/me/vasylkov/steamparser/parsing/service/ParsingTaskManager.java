package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.component.ItemsStorage;
import me.vasylkov.steamparser.parsing.component.ParsingStatus;
import me.vasylkov.steamparser.spring.component.DataInitializer;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParsingTaskManager
{
    private final ItemsStorage itemsStorage;
    private final DataInitializer dataInitializer;
    private final ParsingStatus statusManager;
    private final ParsingService parsingService;
    private final Logger logger;

    public void starParsingProcess()
    {
        if (statusManager.isParsingStarted())
        {
            logger.error("Невозможно начать парсинг так как он уже был начат");
        }

        dataInitializer.init();
        while (!itemsStorage.isEmpty())
        {
            parsingService.parsePage(itemsStorage.getNext());
        }
    }

    public void stopParsingProcess()
    {
        if (!statusManager.isParsingStarted())
        {
            logger.error("Невозможно остановить парсинг пока ты его не начал");
        }
    }
}
