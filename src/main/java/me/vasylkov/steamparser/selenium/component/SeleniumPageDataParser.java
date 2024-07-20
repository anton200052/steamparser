package me.vasylkov.steamparser.selenium.component;

import me.vasylkov.steamparser.parsing.entity.Page;
import org.openqa.selenium.WebDriver;

public interface SeleniumPageDataParser
{
    Page parsePageDataToObject(WebDriver webDriver);
}
