package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.component.ItemDataQueue;
import me.vasylkov.steamparser.parsing.component.ParsingStatus;
import me.vasylkov.steamparser.data.component.DataInitializer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParsingTaskManager
{
    private final ItemDataQueue itemDataQueue;
    private final DataInitializer dataInitializer;
    private final ParsingStatus statusManager;
    private final ParsingService parsingService;
    private final ChromeOptions chromeOptions;
    private final Logger logger;

    public void startParsingProcess()
    {
        if (statusManager.isParsingStarted())
        {
            logger.error("Невозможно начать парсинг так как он уже был начат");
            return;
        }

        dataInitializer.init();

        WebDriver webDriver = new ChromeDriver(chromeOptions);
        while (!itemDataQueue.isEmpty())
        {
            parsingService.parseItemPage(webDriver, itemDataQueue.getNext());
        }
        webDriver.close();
    }

    public void stopParsingProcess()
    {
        if (!statusManager.isParsingStarted())
        {
            logger.error("Невозможно остановить парсинг пока ты его не начал");
        }
    }
}
