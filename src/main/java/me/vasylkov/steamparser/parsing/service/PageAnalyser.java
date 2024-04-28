package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.data.entity.SteamItem;
import me.vasylkov.steamparser.parsing.entity.AnalysingResult;
import me.vasylkov.steamparser.parsing.entity.Listing;
import me.vasylkov.steamparser.parsing.entity.Page;

import java.util.List;

public interface PageAnalyser
{
    AnalysingResult analysePage(Page page, Item item);
}
