package me.vasylkov.steamparser.parsing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
public class AnalysingResult
{
    private List<ProfitableListing> profitableListings;

    public AnalysingResult()
    {
    }
}
