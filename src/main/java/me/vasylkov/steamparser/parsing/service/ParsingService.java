package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.data.entity.Item;
import org.openqa.selenium.WebDriver;
import org.springframework.scheduling.annotation.Async;

public interface ParsingService
{
    void executeAsyncParsingTask();
}
