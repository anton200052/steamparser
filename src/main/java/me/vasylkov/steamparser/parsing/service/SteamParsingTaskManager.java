package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.data.component.ItemQueueManager;
import me.vasylkov.steamparser.data.component.SteamItemsQueueManager;
import me.vasylkov.steamparser.data.entity.SteamItem;
import me.vasylkov.steamparser.general.interfaces.MessagesSender;
import me.vasylkov.steamparser.parsing.component.ParsingStatus;
import me.vasylkov.steamparser.parsing.component.SteamParsingStatus;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.telegram.service.TelegramMessagesSender;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SteamParsingTaskManager implements ParsingTaskManager
{
    private final ItemQueueManager<SteamItem> steamItemsQueue;
    @Qualifier("steamParsingStatus")
    private final ParsingStatus statusManager;
    @Qualifier("steamParsingService")
    private final ParsingService steamParsingService;
    @Qualifier("telegramMessagesSender")
    private final MessagesSender messagesSender;
    private final Logger logger;
    private final ParsingProperties parsingProperties;

    public SteamParsingTaskManager(SteamItemsQueueManager steamItemsQueue, SteamParsingStatus statusManager, SteamParsingService steamParsingService, TelegramMessagesSender messagesSender, Logger logger, ParsingProperties parsingProperties)
    {
        this.steamItemsQueue = steamItemsQueue;
        this.statusManager = statusManager;
        this.steamParsingService = steamParsingService;
        this.messagesSender = messagesSender;
        this.logger = logger;
        this.parsingProperties = parsingProperties;
    }

    @Override
    public void startParsingProcess()
    {
        if (statusManager.isParsingStarted())
        {
            logger.error("Невозможно начать парсинг так как он уже был начат");
            return;
        }

        statusManager.setParsingStarted(true);
        messagesSender.sendMessage("Начинаем парсинг. Доп. информация доступна в консоли приложения");
        int threads = parsingProperties.getThreads();
        logger.info("Начинаем парсинг на {} потоках", threads);

        for (int i = 0; i < threads; i++)
        {
            steamParsingService.executeAsyncParsingTask();
        }
    }

    @Override
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
