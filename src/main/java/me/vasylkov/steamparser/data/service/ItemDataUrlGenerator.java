package me.vasylkov.steamparser.data.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ItemDataUrlGenerator
{
    private final ParsingProperties parsingProperties;

    public String generateListingsUrl(String itemName)
    {
        return "https://steamcommunity.com/market/listings/730/" + encodeItem(itemName) + "?l=english";
    }

    public String generatePriceOverviewApiUrl(String itemName)
    {
        return "https://steamcommunity.com/market/priceoverview/?market_hash_name=" + encodeItem(itemName) + "&appid=730&currency=" + parsingProperties.getCurrencyTypeNum();
    }

    private String encodeItem(String itemName)
    {
        return URLEncoder.encode(itemName, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
