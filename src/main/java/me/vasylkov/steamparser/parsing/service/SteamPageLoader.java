package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SteamPageLoader
{
    private final Logger logger;
    private final ParsingProperties properties;

    public void loadPageByPageNum(WebDriver webDriver, String pageUrl, int pageNum)
    {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(properties.getElementsWaitingDuration()));
        boolean withoutErrors = false;

        while (!withoutErrors)
        {
            if (!webDriver.getCurrentUrl().equals(pageUrl))
            {
                webDriver.get(pageUrl);
            }

            if (hasTableMessageError(wait))
            {
                webDriver.get(pageUrl);
                continue;
            }

            boolean pageChanged = false;
            while (!pageChanged)
            {
                executePageChangerScript(webDriver, pageNum);
                pageChanged = hasPageChangingError(wait, pageNum);
            }

            withoutErrors = true;
        }
    }

    private boolean hasTableMessageError(WebDriverWait webDriverWait)
    {
        try
        {
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".market_listing_table_message")));
            return true;
        }
        catch (TimeoutException ignore)
        {
            return false;
        }
    }

    private void executePageChangerScript(WebDriver webDriver, int pageNum)
    {
        if (pageNum > 1)
        {
            try
            {
                Thread.sleep(properties.getPageChangingDuration() * 1000L);
            }
            catch (InterruptedException e)
            {
                logger.warn("Ошибка при ожидании перед сменой страницы", e);
            }

            JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
            javascriptExecutor.executeScript("g_oSearchResults.GoToPage(" + (pageNum - 1) + ")");
        }
    }

    private boolean hasPageChangingError(WebDriverWait webDriverWait, int pageNum)
    {
        try
        {
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sih_label.sih_label_warning")));
            return false;
        }
        catch (TimeoutException e)
        {
            try
            {
                System.out.println(pageNum + " from");
                webDriverWait.until(driver -> driver.findElement(By.className("info")).getText().startsWith(pageNum + " from"));
                return true;
            }
            catch (TimeoutException | StaleElementReferenceException empty)
            {
                return false;
            }
        }
    }
}
