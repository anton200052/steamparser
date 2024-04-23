package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.data.entity.Item;
import org.openqa.selenium.WebDriver;

public interface ParsingService
{
    void parseItemPage(WebDriver webDriver, Item item);
}
