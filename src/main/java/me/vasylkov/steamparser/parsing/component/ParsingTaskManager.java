package me.vasylkov.steamparser.parsing.component;

import me.vasylkov.steamparser.common.abstraction.MessagesSender;
import me.vasylkov.steamparser.data.component.ItemQueueManager;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.parsing.service.ParsingService;
import me.vasylkov.steamparser.parsing.service.SeleniumSteamParsingService;
import me.vasylkov.steamparser.telegram.service.TelegramMessagesSender;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ParsingTaskManager
{
    @Qualifier("parsingStatus")
    private final ParsingStatus statusManager;
    @Qualifier("steamRenderParsingService")
    private final ParsingService steamRenderParsingService;
    @Qualifier("telegramMessagesSender")
    private final MessagesSender messagesSender;
    private final Logger logger;
    private final ParsingProperties parsingProperties;
    private final ItemQueueManager steamItemQueueManager;

    public ParsingTaskManager(ParsingStatus statusManager, ParsingService steamRenderParsingService, TelegramMessagesSender messagesSender, Logger logger, ParsingProperties parsingProperties, ItemQueueManager steamItemQueueManager)
    {
        this.statusManager = statusManager;
        this.steamRenderParsingService = steamRenderParsingService;
        this.messagesSender = messagesSender;
        this.logger = logger;
        this.parsingProperties = parsingProperties;
        this.steamItemQueueManager = steamItemQueueManager;
    }

    public void startParsingProcess()
    {
        if (statusManager.isParsingStarted())
        {
            logger.error("Невозможно начать парсинг так как он уже был начат");
            return;
        }

        steamItemQueueManager.updatePricesInQueue();
        statusManager.setParsingStarted(true);
        messagesSender.sendMessage("Начинаем парсинг. Доп. информация доступна в консоли приложения");
        int threads = parsingProperties.getThreads();
        logger.info("Начинаем парсинг на {} потоках", threads);

        for (int i = 0; i < threads; i++)
        {
            steamRenderParsingService.executeAsyncParsingTask();
        }
    }

    public void stopParsingProcess()
    {
        if (!statusManager.isParsingStarted())
        {
            logger.error("Невозможно остановить парсинг пока ты его не начал");
            return;
        }

        statusManager.setParsingStarted(false);
        messagesSender.sendMessage("Останавливаем парсинг!");
    }
}
