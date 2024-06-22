package me.vasylkov.steamparser.selenium.component;

import me.vasylkov.steamparser.selenium.configuration.SeleniumProperties;
import me.vasylkov.steamparser.selenium.entity.ProxyWrapper;
import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class WebDriverProxyChanger
{
    @Qualifier("chromeDriverFactory")
    private final DriverFactory driverFactory;
    private final ProxyManager proxyManager;
    private final SeleniumProperties properties;

    public WebDriverProxyChanger(DriverFactory driverFactory, ProxyManager proxyManager, SeleniumProperties properties)
    {
        this.driverFactory = driverFactory;
        this.proxyManager = proxyManager;
        this.properties = properties;
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
                driver.close();
                WebDriverWrapper newWebDriverWrapper = driverFactory.createWebDriver(unblockedProxy);
                webDriverWrapper.setDriver(newWebDriverWrapper.getDriver());
                webDriverWrapper.setProxy(newWebDriverWrapper.getProxy());
                webDriverWrapper.setWebDriverWait(newWebDriverWrapper.getWebDriverWait());
            }
        }
    }
}
