package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.httpclient.entity.SteamItem;
import me.vasylkov.steamparser.parsing.component.ItemsStorage;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ParsingService
{
    private final Logger logger;
    private final ItemsStorage linkStorage;
    private final ChromeOptions options;
    private final PageCreator pageCreator;

    public void parseItemPage(SteamItem item)
    {
        logger.info("Начинаем парсинг предмета {}", item.getHashName());
        try
        {
            WebDriver driver = new ChromeDriver(options);
            generatePageObject(driver, item.getUrl(), 3);
        }
        catch (WebDriverException e)
        {
            logger.error("Критическая ошибка при парсинге предмета {}, парсинг предмета будет остановлен", item.getHashName());
            e.printStackTrace();
        }
    }

    private void generatePageObject(WebDriver webDriver, String pageUrl, int pageNum)
    {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        boolean conditionMet = false;
        boolean isFirstAttempt = true;

        while (!conditionMet)
        {
            if (!isFirstAttempt || !webDriver.getCurrentUrl().equals(pageUrl))
            {
                webDriver.get(pageUrl);
            }

            isFirstAttempt = false;

            try
            {
                // Проверяем наличие ошибки загрузки страницы до того, как пытаемся перейти на следующую страницу
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".market_listing_table_message")));
                continue;
            }
            catch (TimeoutException ignore)
            {
                // Если ошибка загрузки листинга не обнаружена, продолжаем
            }

            if (pageNum > 1)
            {
                {
                    JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
                    javascriptExecutor.executeScript("g_oSearchResults.GoToPage(" + (pageNum - 1) + ")");
                }
            }

            // Проверяем наличие ошибок после перехода на страницу
            try
            {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sih_label.sih_label_warning")));
            }
            catch (TimeoutException e)
            {
                try
                {
                    System.out.println(pageNum + " from");
                    wait.until(driver -> driver.findElement(By.className("info")).getText().startsWith(pageNum + " from"));
                    conditionMet = true;
                }
                catch (TimeoutException empty)
                {
                }
                logger.info("Condition met: " + conditionMet);
            }
        }
        System.out.println(pageCreator.createPage(webDriver));
    }
}
