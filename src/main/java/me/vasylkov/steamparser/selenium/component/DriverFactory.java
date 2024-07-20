package me.vasylkov.steamparser.selenium.component;

import me.vasylkov.steamparser.selenium.entity.SeleniumProxyWrapper;
import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;

public interface DriverFactory
{
    WebDriverWrapper createWebDriver(SeleniumProxyWrapper seleniumProxyWrapper);
}
