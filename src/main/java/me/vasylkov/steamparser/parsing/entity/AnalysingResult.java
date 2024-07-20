package me.vasylkov.steamparser.parsing.entity;

import java.util.List;

public interface AnalysingResult
{
    List<Listing> getProfitableListings();
    void setProfitableListings(List<Listing> listings);
}
