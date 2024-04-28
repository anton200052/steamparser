package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.data.entity.SteamItem;
import me.vasylkov.steamparser.general.interfaces.MessagesSender;
import me.vasylkov.steamparser.parsing.entity.AnalysingResult;
import me.vasylkov.steamparser.parsing.entity.Listing;
import me.vasylkov.steamparser.parsing.entity.SteamPage;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public SteamParsingService(Logger logger, PageDataParser pageDataParser, SteamPageLoader steamPageLoader, PageAnalyser pageAnalyser, MessagesSender messagesSender)
    {
        this.logger = logger;
        this.pageDataParser = pageDataParser;
        this.steamPageLoader = steamPageLoader;
        this.pageAnalyser = pageAnalyser;
        this.messagesSender = messagesSender;
    }

    @Override
    public void parseItemPage(WebDriver webDriver, Item item)
    {
        SteamItem steamItem = (SteamItem) item;
        logger.info("Начинаем парсинг предмета {}", steamItem.getHashName());
        try
        {
            boolean priceExceedsMarkup = false;
            int currentPageNum = 1;

            while (!priceExceedsMarkup)
            {
                steamPageLoader.loadPageByPageNum(webDriver, steamItem.getListingsUrl(), currentPageNum);
                SteamPage steamPage = (SteamPage) pageDataParser.parsePageDataToObject(webDriver);
                AnalysingResult analysingResult = pageAnalyser.analysePage(steamPage, steamItem);

                for (Listing listing : analysingResult.getProfitableListings())
                {
                    messagesSender.sendProfitableItemData(listing.getImgUrl(), listing.getHashName(), item.getAveragePrice(), listing.getPrice(), currentPageNum, listing.getStickers(), listing.getTotalStickersPrice(), listing.getPriceWithStickersMarkup(), listing.getStickersMarkupPercentage());
                }

                priceExceedsMarkup = analysingResult.isPriceExceedsMaxItemMarkup();
                currentPageNum = currentPageNum + 1;
            }
        }
        catch (WebDriverException e)
        {
            e.printStackTrace();
            logger.error("Критическая ошибка при парсинге предмета {}, парсинг предмета будет остановлен", steamItem.getHashName());
        }
    }

}
