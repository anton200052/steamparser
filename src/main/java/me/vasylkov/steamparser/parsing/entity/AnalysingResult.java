package me.vasylkov.steamparser.parsing.entity;

import java.util.List;

public interface AnalysingResult
{
    List<Listing> getProfitableListings();
    void setProfitableListings(List<Listing> listings);

    boolean isPriceExceedsMaxItemMarkup();
    void setPriceExceedsMaxItemMarkup(boolean b);
}
