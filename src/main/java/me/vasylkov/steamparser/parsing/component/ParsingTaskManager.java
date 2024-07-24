package me.vasylkov.steamparser.parsing.component;

import me.vasylkov.steamparser.common.abstraction.MessagesSender;
import me.vasylkov.steamparser.data.component.ItemQueueManager;
import me.vasylkov.steamparser.data.entity.SteamItem;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.parsing.service.ParsingService;
import me.vasylkov.steamparser.parsing.service.SeleniumSteamParsingService;
import me.vasylkov.steamparser.selenium.configuration.SeleniumProperties;
import me.vasylkov.steamparser.telegram.service.TelegramMessagesSender;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ParsingTaskManager
{
    @Qualifier("parsingStatus")
    private final ParsingStatus statusManager;
    @Qualifier("seleniumSteamParsingService")
    private final ParsingService steamParsingService;
    @Qualifier("telegramMessagesSender")
    private final MessagesSender messagesSender;
    private final Logger logger;
    private final ParsingProperties parsingProperties;
    private final ItemQueueManager<SteamItem> steamItemQueueManager;

    public ParsingTaskManager(ParsingStatus statusManager, SeleniumSteamParsingService seleniumSteamParsingService, TelegramMessagesSender messagesSender, Logger logger, ParsingProperties parsingProperties, ItemQueueManager<SteamItem> steamItemQueueManager)
    {
        this.statusManager = statusManager;
        this.steamParsingService = seleniumSteamParsingService;
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

        steamItemQueueManager.updateItemsPricesAndLinks();
        statusManager.setParsingStarted(true);
        messagesSender.sendMessage("Начинаем парсинг. Доп. информация доступна в консоли приложения");
        int threads = parsingProperties.getThreads();
        logger.info("Начинаем парсинг на {} потоках", threads);

        for (int i = 0; i < threads; i++)
        {
            steamParsingService.executeParsingTask();
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
