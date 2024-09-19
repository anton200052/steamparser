package me.vasylkov.steamparser.parsing.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SteamAnalysingResult extends AnalysingResult
{
    public SteamAnalysingResult(List<ListingWithStickersMarkup> profitableListings)
    {
        super(profitableListings);
    }

    public SteamAnalysingResult()
    {
    }
}
