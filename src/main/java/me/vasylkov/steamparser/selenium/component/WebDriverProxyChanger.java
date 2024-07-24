package me.vasylkov.steamparser.selenium.component;

import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.selenium.configuration.SeleniumProperties;
import me.vasylkov.steamparser.selenium.entity.SeleniumProxyWrapper;
import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class WebDriverProxyChanger
{
    @Qualifier("chromeDriverFactory")
    private final DriverFactory driverFactory;
    private final SeleniumProxyManager seleniumProxyManager;
    private final ParsingProperties parsingProperties;
    private final SeleniumTempFilesCleaner seleniumTempFilesCleaner;

    public WebDriverProxyChanger(DriverFactory driverFactory, SeleniumProxyManager seleniumProxyManager, ParsingProperties parsingProperties, SeleniumTempFilesCleaner seleniumTempFilesCleaner)
    {
        this.driverFactory = driverFactory;
        this.seleniumProxyManager = seleniumProxyManager;
        this.parsingProperties = parsingProperties;
        this.seleniumTempFilesCleaner = seleniumTempFilesCleaner;
    }

    public void changeProxyAndWebDriver(WebDriverWrapper webDriverWrapper)
    {
        if (parsingProperties.isEnableProxy())
        {
            WebDriver driver = webDriverWrapper.getDriver();
            SeleniumProxyWrapper proxy = webDriverWrapper.getProxy();
            if (proxy != null)
            {
                seleniumProxyManager.blockProxy(proxy);
            }

            SeleniumProxyWrapper unblockedProxy = seleniumProxyManager.getAvailableProxy();

            if (proxy != null || unblockedProxy != null)
            {
                driver.quit();
                for (Path tempFile : webDriverWrapper.getTempFiles())
                {
                    try
                    {
                        if (Files.isDirectory(tempFile))
                        {
                            seleniumTempFilesCleaner.deleteDirectoryAndContents(tempFile);
                        }
                        else
                        {
                            Files.deleteIfExists(tempFile);
                        }
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException("Failed to delete temp file", e);
                    }
                }

                WebDriverWrapper newWebDriverWrapper = driverFactory.createWebDriver(unblockedProxy);
                webDriverWrapper.setDriver(newWebDriverWrapper.getDriver());
                webDriverWrapper.setProxy(newWebDriverWrapper.getProxy());
                webDriverWrapper.setWebDriverWait(newWebDriverWrapper.getWebDriverWait());
                webDriverWrapper.setTempFiles(newWebDriverWrapper.getTempFiles());
            }
        }
    }
}
