package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.component.SteamItemsQueue;
import me.vasylkov.steamparser.parsing.component.SteamParsingStatus;
import me.vasylkov.steamparser.data.component.DataInitializer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SteamParsingTaskManager implements ParsingTaskManager
{
    private final SteamItemsQueue steamItemsQueue;
    private final DataInitializer dataInitializer;
    private final SteamParsingStatus statusManager;
    private final SteamParsingService steamParsingService;
    private final ChromeOptions chromeOptions;
    private final Logger logger;

    @Override
    public void startParsingProcess()
    {
        if (statusManager.isParsingStarted())
        {
            logger.error("Невозможно начать парсинг так как он уже был начат");
            return;
        }

        dataInitializer.init();

        WebDriver webDriver = new ChromeDriver(chromeOptions);
        while (!steamItemsQueue.isEmpty())
        {
            steamParsingService.parseItemPage(webDriver, steamItemsQueue.getNext());
        }
        webDriver.close();
    }

    @Override
    public void stopParsingProcess()
    {
        if (!statusManager.isParsingStarted())
        {
            logger.error("Невозможно остановить парсинг пока ты его не начал");
        }
    }
}
