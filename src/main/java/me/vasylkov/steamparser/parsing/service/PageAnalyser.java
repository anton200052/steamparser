package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.parsing.entity.SteamAnalysingResult;
import me.vasylkov.steamparser.parsing.entity.Page;

public interface PageAnalyser
{
    SteamAnalysingResult analysePage(Page page, Item item);
}
