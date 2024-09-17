package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.data.component.ItemQueueManager;
import me.vasylkov.steamparser.data.component.UrlGenerator;
import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.data.entity.SteamItem;
import me.vasylkov.steamparser.common.abstraction.MessagesSender;
import me.vasylkov.steamparser.parsing.component.PageAnalyser;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.selenium.component.SeleniumPageDataParser;
import me.vasylkov.steamparser.selenium.component.SteamSeleniumPageLoader;
import me.vasylkov.steamparser.parsing.component.ParsingStatus;
import me.vasylkov.steamparser.parsing.entity.AnalysingResult;
import me.vasylkov.steamparser.parsing.entity.Listing;
import me.vasylkov.steamparser.parsing.entity.SteamPage;
import me.vasylkov.steamparser.selenium.component.ChromeDriverFactory;
import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SeleniumSteamParsingService implements ParsingService
{
    private final Logger logger;

    @Qualifier("steamSeleniumPageDataParser")
    private final SeleniumPageDataParser seleniumPageDataParser;
    private final SteamSeleniumPageLoader steamPageLoader;
    @Qualifier("steamPageAnalyser")
    private final PageAnalyser pageAnalyser;
    @Qualifier("telegramMessagesSender")
    private final MessagesSender messagesSender;
    @Qualifier("parsingStatus")
    private final ParsingStatus parsingStatus;
    private final ParsingProperties parsingProperties;
    private final ItemQueueManager<SteamItem> itemQueueManager;
    private final ChromeDriverFactory chromeDriverFactory;
    private final UrlGenerator urlGenerator;

    public SeleniumSteamParsingService(Logger logger, SeleniumPageDataParser seleniumPageDataParser, SteamSeleniumPageLoader steamPageLoader, PageAnalyser pageAnalyser, MessagesSender messagesSender, ParsingStatus parsingStatus, ParsingProperties parsingProperties, ItemQueueManager<SteamItem> itemQueueManager, ChromeDriverFactory chromeDriverFactory, UrlGenerator urlGenerator)
    {
        this.logger = logger;
        this.seleniumPageDataParser = seleniumPageDataParser;
        this.steamPageLoader = steamPageLoader;
        this.pageAnalyser = pageAnalyser;
        this.messagesSender = messagesSender;
        this.parsingStatus = parsingStatus;
        this.parsingProperties = parsingProperties;
        this.itemQueueManager = itemQueueManager;
        this.chromeDriverFactory = chromeDriverFactory;
        this.urlGenerator = urlGenerator;
    }

    @Async
    @Override
    public void executeParsingTask()
    {
        parseItems();
    }

    private void parseItems()
    {
        WebDriverWrapper webDriverWrapper = null;
        try
        {
            webDriverWrapper = chromeDriverFactory.createWebDriver(null);
            Item lastAvailable = null;
            while (parsingStatus.isParsingStarted())
            {
                lastAvailable = processNextAvailableItem(webDriverWrapper, lastAvailable);
                if (lastAvailable == null)
                {
                    return;
                }
            }
        }
        catch (Exception e)
        {
            logger.info("Произошла критическая ошибка парсинга. К сожалению поток будет остановлен", e);
        }
        finally
        {
            if (webDriverWrapper != null)
            {
                webDriverWrapper.getDriver().quit();
            }
        }
    }

    private Item processNextAvailableItem(WebDriverWrapper webDriverWrapper, Item lastAvailable)
    {
        Item currentAvailable = getCurrentAvailable(lastAvailable);
        if (currentAvailable != null)
        {
            parseItem(currentAvailable, webDriverWrapper);

            if (parsingProperties.isCycle())
            {
                itemQueueManager.moveItemToLastAndUnblock((SteamItem) currentAvailable);
            }
        }

        return currentAvailable;
    }

    private Item getCurrentAvailable(Item lastAvailable)
    {
        Item currentAvailable = itemQueueManager.getAndBlockFirstAvailableItem();
        if (currentAvailable == null)
        {
            if (lastAvailable == null || !parsingProperties.isCycle())
            {
                logger.info("Задач для потока {} нет, поток не будет продолжать работу", Thread.currentThread().getId());
                return null;
            }
            currentAvailable = lastAvailable;
        }
        return currentAvailable;
    }

    private void parseItem(Item item, WebDriverWrapper webDriverWrapper)
    {
        SteamItem steamItem = (SteamItem) item;
        String listingsUrl = urlGenerator.generateListingsUrl(item.getHashName());
        logger.info("Начинаем парсинг предмета {}", steamItem.getHashName());
        int currentPageNum = 1;

        while (currentPageNum <= item.getMaximalPage())
        {
            if (!parsingStatus.isParsingStarted())
            {
                return;
            }

            steamPageLoader.loadPageByPageNum(webDriverWrapper, listingsUrl, currentPageNum);
            SteamPage steamPage = (SteamPage) seleniumPageDataParser.parsePageDataToObject(webDriverWrapper.getDriver());
            AnalysingResult steamAnalysingResult = pageAnalyser.analysePage(steamPage, steamItem);

            for (Listing listing : steamAnalysingResult.getProfitableListings())
            {
                messagesSender.sendProfitableItemData(listing.getImgUrl(), listing.getHashName(), item.getAveragePrice(), listing.getPrice(), currentPageNum, listing.getStickers(), listing.getTotalStickersPrice(), listing.getPriceWithStickersMarkup(), listing.getStickersMarkupPercentage());
            }

            currentPageNum = currentPageNum + 1;
        }
    }
}
