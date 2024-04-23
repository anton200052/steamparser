package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.parsing.entity.Page;
import me.vasylkov.steamparser.parsing.entity.PageNumType;
import me.vasylkov.steamparser.parsing.entity.SteamPage;
import org.openqa.selenium.WebDriver;

public interface PageDataParser
{
    Page parsePageDataToObject(WebDriver webDriver);
}
