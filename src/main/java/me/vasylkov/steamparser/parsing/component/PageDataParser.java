package me.vasylkov.steamparser.parsing.component;

import me.vasylkov.steamparser.parsing.entity.Page;
import org.openqa.selenium.WebDriver;

public interface PageDataParser
{
    Page parsePageDataToObject(WebDriver webDriver);
}
