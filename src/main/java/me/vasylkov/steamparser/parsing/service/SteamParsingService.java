package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.data.component.ItemQueueManager;
import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.data.entity.SteamItem;
import me.vasylkov.steamparser.general.interfaces.MessagesSender;
import me.vasylkov.steamparser.parsing.component.ParsingStatus;
import me.vasylkov.steamparser.parsing.component.SteamParsingStatus;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.parsing.entity.*;
import me.vasylkov.steamparser.selenium.component.ChromeDriverFactory;
import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SteamParsingService implements ParsingService
{
    private final Logger logger;

    @Qualifier("steamPageDataParser")
    private final PageDataParser pageDataParser;
    private final SteamPageLoader steamPageLoader;
    @Qualifier("steamPageAnalyser")
    private final PageAnalyser pageAnalyser;
    @Qualifier("telegramMessagesSender")
    private final MessagesSender messagesSender;
    @Qualifier("steamParsingStatus")
    private final ParsingStatus parsingStatus;
    private final ParsingProperties parsingProperties;
    private final ItemQueueManager<SteamItem> itemQueueManager;
    private final ChromeDriverFactory chromeDriverFactory;

    public SteamParsingService(Logger logger, PageDataParser pageDataParser, SteamPageLoader steamPageLoader, PageAnalyser pageAnalyser, MessagesSender messagesSender, SteamParsingStatus parsingStatus, ParsingProperties parsingProperties, ItemQueueManager<SteamItem> itemQueueManager, ChromeDriverFactory chromeDriverFactory)
    {
        this.logger = logger;
        this.pageDataParser = pageDataParser;
        this.steamPageLoader = steamPageLoader;
        this.pageAnalyser = pageAnalyser;
        this.messagesSender = messagesSender;
        this.parsingStatus = parsingStatus;
        this.parsingProperties = parsingProperties;
        this.itemQueueManager = itemQueueManager;
        this.chromeDriverFactory = chromeDriverFactory;
    }

    @Async
    @Override
    public void executeAsyncParsingTask()
    {
        WebDriverWrapper webDriverWrapper = null;
        try
        {
            webDriverWrapper = chromeDriverFactory.createWebDriver(null);
            Item lastAvailable = null;
            while (parsingStatus.isParsingStarted())
            {
                Item currentAvailable = itemQueueManager.getNextAvailable();

                if (currentAvailable == null)
                {
                    if (lastAvailable == null || !parsingProperties.isCycling())
                    {
                        logger.info("Задач для потока {} нет, поток не будет продолжать работу", Thread.currentThread().getId());
                        webDriverWrapper.getDriver().close();
                        return;
                    }
                    currentAvailable = lastAvailable;
                }

                currentAvailable.setAvailable(false);

                parseItem(currentAvailable, webDriverWrapper);

                if (parsingProperties.isCycling())
                {
                    currentAvailable.setAvailable(true);
                    itemQueueManager.moveToLast((SteamItem) currentAvailable);
                }

                lastAvailable = currentAvailable;
            }
        }
        catch (Exception e)
        {
            logger.info("Exception occurred while parsing task", e);
        }
        finally
        {
            if (webDriverWrapper != null)
            {
                webDriverWrapper.getDriver().close();
            }
        }
    }

    private void parseItem(Item item, WebDriverWrapper webDriverWrapper)
    {
        logger.info("Thread: {}", Thread.currentThread().getId());
        SteamItem steamItem = (SteamItem) item;
        logger.info("Начинаем парсинг предмета {}", steamItem.getHashName());
        boolean priceExceedsMarkup = false;
        int currentPageNum = 1;

        while (!priceExceedsMarkup)
        {
            if (!parsingStatus.isParsingStarted())
            {
                return;
            }

            steamPageLoader.loadPageByPageNum(webDriverWrapper, steamItem.getListingsUrl(), currentPageNum);
            SteamPage steamPage = (SteamPage) pageDataParser.parsePageDataToObject(webDriverWrapper.getDriver());
            AnalysingResult steamAnalysingResult = pageAnalyser.analysePage(steamPage, steamItem);

            for (Listing listing : steamAnalysingResult.getProfitableListings())
            {
                messagesSender.sendProfitableItemData(listing.getImgUrl(), listing.getHashName(), item.getAveragePrice(), listing.getPrice(), currentPageNum, listing.getStickers(), listing.getTotalStickersPrice(), listing.getPriceWithStickersMarkup(), listing.getStickersMarkupPercentage());
            }

            priceExceedsMarkup = steamAnalysingResult.isPriceExceedsMaxItemMarkup();
            currentPageNum = currentPageNum + 1;
        }
    }
}
