package me.vasylkov.steamparser.selenium.component;

import me.vasylkov.steamparser.selenium.entity.ProxyWrapper;
import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;
import org.openqa.selenium.WebDriver;

public interface DriverFactory
{
    WebDriverWrapper createWebDriver(ProxyWrapper proxyWrapper);
}
