package me.vasylkov.steamparser.selenium.component;

import me.vasylkov.steamparser.general.component.TempFilesCleaner;
import me.vasylkov.steamparser.selenium.configuration.SeleniumProperties;
import me.vasylkov.steamparser.selenium.entity.ProxyWrapper;
import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class WebDriverProxyChanger
{
    @Qualifier("chromeDriverFactory")
    private final DriverFactory driverFactory;
    private final ProxyManager proxyManager;
    private final SeleniumProperties properties;
    private final TempFilesCleaner tempFilesCleaner;

    public WebDriverProxyChanger(DriverFactory driverFactory, ProxyManager proxyManager, SeleniumProperties properties, TempFilesCleaner tempFilesCleaner)
    {
        this.driverFactory = driverFactory;
        this.proxyManager = proxyManager;
        this.properties = properties;
        this.tempFilesCleaner = tempFilesCleaner;
    }

    public void changeProxyAndWebDriver(WebDriverWrapper webDriverWrapper)
    {
        if (properties.isProxiesEnabled())
        {
            WebDriver driver = webDriverWrapper.getDriver();
            ProxyWrapper proxy = webDriverWrapper.getProxy();
            if (proxy != null)
            {
                proxyManager.blockProxy(proxy);
            }

            ProxyWrapper unblockedProxy = proxyManager.getAvailableProxy();

            if (proxy != null || unblockedProxy != null)
            {
                driver.quit();
                for (Path tempFile : webDriverWrapper.getTempFiles())
                {
                    try
                    {
                        if (Files.isDirectory(tempFile))
                        {
                            tempFilesCleaner.deleteDirectoryAndContents(tempFile);
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
