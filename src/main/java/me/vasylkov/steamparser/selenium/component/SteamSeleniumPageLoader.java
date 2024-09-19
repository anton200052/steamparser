package me.vasylkov.steamparser.selenium.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.enums.PageChangingResult;
import me.vasylkov.steamparser.selenium.configuration.SeleniumProperties;
import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SteamSeleniumPageLoader implements SeleniumPageLoader
{
    private final Logger logger;
    private final SeleniumProperties seleniumProperties;
    private final WebDriverProxyChanger webDriverProxyChanger;

    @Override
    public void loadPageByPageNum(WebDriverWrapper webDriverWrapper, String pageUrl, int pageNum)
    {
        PageChangingResult pageChangingResult;
        do
        {
            loadListings(webDriverWrapper, pageUrl);
            pageChangingResult = changeAndLoadPage(webDriverWrapper, pageNum);

            if (pageChangingResult == PageChangingResult.TOO_MANY_REQUESTS || !isExtensionLoaded(webDriverWrapper.getWebDriverWait()))
            {
                webDriverProxyChanger.changeProxyAndWebDriver(webDriverWrapper);
            }
        } while (pageChangingResult != PageChangingResult.SUCCESS);
    }

    private PageChangingResult changeAndLoadPage(WebDriverWrapper webDriverWrapper, int pageNum)
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

    private boolean isExtensionLoaded(WebDriverWait webDriverWait)
    {
        try
        {
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sih")));
            return true;
        }
        catch (TimeoutException ignore)
        {
            return false;
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
            Thread.sleep(seleniumProperties.getPageChangingDuration() * 1000L);
            JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
            javascriptExecutor.executeScript("g_oSearchResults.GoToPage(" + (pageNum - 1) + ")");
        }
        catch (InterruptedException e)
        {
            logger.error("Ошибка при ожидании перед сменой страницы", e);
        }
        catch (JavascriptException e)
        {
            logger.error("Ошибка при смене страницы листингов", e);
        }
    }

    private PageChangingResult getPageChangingResult(WebDriverWait webDriverWait, int pageNum)
    {
        try
        {
            WebElement warningElement = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sih_label.sih_label_warning")));
            String warningText = warningElement.getText();
            if (warningText.contains("Steam error: 429 Too Many Requests"))
            {
                return PageChangingResult.TOO_MANY_REQUESTS;
            }
            else
            {
                return PageChangingResult.STEAM_ERROR;
            }
        }
        catch (TimeoutException e)
        {
            try
            {
                logger.info("Страница: {}", pageNum);
                webDriverWait.until(driver -> driver.findElement(By.className("info")).getText().startsWith(pageNum + " from"));
                return PageChangingResult.SUCCESS;
            }
            catch (TimeoutException | StaleElementReferenceException ex)
            {
                return PageChangingResult.UNKNOWN_ERROR;
            }
        }
        catch (StaleElementReferenceException e)
        {
            return PageChangingResult.UNKNOWN_ERROR;
        }
    }
}
