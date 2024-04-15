package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.httpclient.entity.ItemToParseData;
import me.vasylkov.steamparser.parsing.entity.Page;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParsingService
{
    private final Logger logger;
    private final PageDataMapper pageDataMapper;
    private final PageLoader pageLoader;

    public void parseItemPage(WebDriver webDriver, ItemToParseData item)
    {
        logger.info("Начинаем парсинг предмета {}", item.getHashName());
        try
        {
            pageLoader.loadPageByPageNum(webDriver, item.getListingsUrl(), 1);
            Page page = pageDataMapper.mapPageDataToObject(webDriver);
        }
        catch (WebDriverException e)
        {
            e.printStackTrace();
            logger.error("Критическая ошибка при парсинге предмета {}, парсинг предмета будет остановлен", item.getHashName());
        }
    }

}
