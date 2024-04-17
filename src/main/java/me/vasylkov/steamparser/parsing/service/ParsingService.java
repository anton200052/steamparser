package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.entity.ItemData;
import me.vasylkov.steamparser.general.interfaces.MessagesSender;
import me.vasylkov.steamparser.parsing.entity.AnalysingResult;
import me.vasylkov.steamparser.parsing.entity.Listing;
import me.vasylkov.steamparser.parsing.entity.Page;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParsingService
{
    private final Logger logger;
    private final PageDataParser pageDataParser;
    private final PageLoader pageLoader;
    private final PageAnalyser pageAnalyser;
    private final MessagesSender messagesSender;

    public void parseItemPage(WebDriver webDriver, ItemData itemData)
    {
        logger.info("Начинаем парсинг предмета {}", itemData.getHashName());
        messagesSender.sendMessage("1");
        try
        {
            boolean priceExceedsMarkup = false;
            int currentPageNum = 1;

            while (!priceExceedsMarkup)
            {
                pageLoader.loadPageByPageNum(webDriver, itemData.getListingsUrl(), currentPageNum);
                Page page = pageDataParser.parsePageDataToObject(webDriver);
                AnalysingResult analysingResult = pageAnalyser.analysePage(page, itemData);

                for (Listing listing : analysingResult.getProfitableListings())
                {
                    messagesSender.sendMessage(listing.toString());
                }

                priceExceedsMarkup = analysingResult.isPriceExceedsMaxItemMarkup();
                currentPageNum = page.getCurrentPage() + 1;
            }
        }
        catch (WebDriverException e)
        {
            e.printStackTrace();
            logger.error("Критическая ошибка при парсинге предмета {}, парсинг предмета будет остановлен", itemData.getHashName());
        }
    }

}
