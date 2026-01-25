package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.config_data.component.ItemQueueManager;
import me.vasylkov.steamparser.parsing.component.ParsingStatus;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.parsing.exception.ParsingRunningException;
import me.vasylkov.steamparser.notificator.service.TelegramMessagesSender;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ParsingTaskService
{
    @Qualifier("parsingStatus")
    private final ParsingStatus statusManager;
    private final SteamRenderParsingService steamRenderParsingService;
    private final Logger logger;
    private final ParsingProperties parsingProperties;
    private final ItemQueueManager steamItemQueueManager;

    public ParsingTaskService(ParsingStatus statusManager, SteamRenderParsingService steamRenderParsingService, TelegramMessagesSender messagesSender, Logger logger, ParsingProperties parsingProperties, ItemQueueManager steamItemQueueManager)
    {
        this.statusManager = statusManager;
        this.steamRenderParsingService = steamRenderParsingService;
        this.logger = logger;
        this.parsingProperties = parsingProperties;
        this.steamItemQueueManager = steamItemQueueManager;
    }

    public void startParsingProcess()
    {
        if (statusManager.isParsingStarted())
        {
            throw new ParsingRunningException("Parsing already started");
        }

        steamItemQueueManager.refreshItemsQueue();
        statusManager.setParsingStarted(true);
        int threads = parsingProperties.getThreads();

        logger.info("Starting parsing process using {} threads", threads);
        startParallelParsingTasks(threads, steamRenderParsingService);
    }

    public void stopParsingProcess()
    {
        if (!statusManager.isParsingStarted())
        {
            throw new ParsingRunningException("Parsing not started yet");
        }

        statusManager.setParsingStarted(false);
    }

    private void startParallelParsingTasks(int threads, ParsingService parsingService) {
        for (int i = 0; i < threads; i++)
        {
            parsingService.executeParsingTask();
        }
    }
}
