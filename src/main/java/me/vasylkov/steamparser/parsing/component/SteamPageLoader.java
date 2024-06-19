package me.vasylkov.steamparser.parsing.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.parsing.entity.PageLoadResult;
import me.vasylkov.steamparser.selenium.component.WebDriverProxyChanger;
import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class SteamPageLoader implements PageLoader
{
    private final Logger logger;
    private final ParsingProperties properties;
    private final WebDriverProxyChanger webDriverProxyChanger;

    @Override
    public void loadPageByPageNum(WebDriverWrapper webDriverWrapper, String pageUrl, int pageNum)
    {
        PageLoadResult pageLoadResult = null;
        do
        {
            if (pageLoadResult == PageLoadResult.TOO_MANY_REQUESTS)
            {
                webDriverProxyChanger.changeProxyAndWebDriver(webDriverWrapper);
            }
            loadListings(webDriverWrapper, pageUrl);
            pageLoadResult = changePage(webDriverWrapper, pageNum);
        } while (pageLoadResult != PageLoadResult.SUCCESS);
    }

    private PageLoadResult changePage(WebDriverWrapper webDriverWrapper, int pageNum)
    {
        executePageChangerScript(webDriverWrapper.getDriver(), pageNum);
        return getPageChangingResult(webDriverWrapper.getWebDriverWait(), pageNum);
    }

    private void loadListings(WebDriverWrapper webDriverWrapper, String pageUrl)
    {
        WebDriver webDriver = webDriverWrapper.getDriver();
        while (!webDriver.getCurrentUrl().equals(pageUrl) || !hasListings(webDriverWrapper.getWebDriverWait()))
        {
            webDriver.get(pageUrl);
        }
    }

    private boolean hasListings(WebDriverWait webDriverWait)
    {
        try
        {
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#searchResultsRows")));
            return true;
        }
        catch (TimeoutException ignore)
        {
            return false;
        }
    }

    private void executePageChangerScript(WebDriver webDriver, int pageNum)
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

    private PageLoadResult getPageChangingResult(WebDriverWait webDriverWait, int pageNum)
    {
        try
        {
            WebElement warningElement = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sih_label.sih_label_warning")));
            String warningText = warningElement.getText();
            if (warningText.contains("Steam error: 429 Too Many Requests"))
            {
                return PageLoadResult.TOO_MANY_REQUESTS;
            }
            else
            {
                return PageLoadResult.STEAM_ERROR;
            }
        }
        catch (TimeoutException e)
        {
            try
            {
                logger.info("Страница: {}", pageNum);
                webDriverWait.until(driver -> driver.findElement(By.className("info")).getText().startsWith(pageNum + " from"));
                return PageLoadResult.SUCCESS;
            }
            catch (TimeoutException | StaleElementReferenceException empty)
            {
                return PageLoadResult.UNKNOWN_ERROR;
            }
        }
    }
}
