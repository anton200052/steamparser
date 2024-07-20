package me.vasylkov.steamparser.selenium.component;

import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;

public interface SeleniumPageLoader
{
    void loadPageByPageNum(WebDriverWrapper webDriverWrapper, String pageUrl, int pageNum);
}
