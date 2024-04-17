package me.vasylkov.steamparser.parsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnalysingResult
{
    private List<Listing> profitableListings;
    private boolean priceExceedsMaxItemMarkup;
}
