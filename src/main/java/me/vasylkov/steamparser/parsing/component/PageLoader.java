package me.vasylkov.steamparser.parsing.component;

import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;

public interface PageLoader
{
    void loadPageByPageNum(WebDriverWrapper webDriverWrapper, String pageUrl, int pageNum);
}
