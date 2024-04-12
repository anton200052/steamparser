package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ItemUrlGenerator
{
    public String generateListingsUrl(String itemName)
    {
        return "https://steamcommunity.com/market/listings/730/" + encodeItem(itemName);
    }

    public String generatePriceLink(String itemName)
    {
        return "https://steamcommunity.com/market/priceoverview/?market_hash_name=" + encodeItem(itemName) + "&appid=730&currency=18";
    }

    private String encodeItem(String itemName)
    {
        return URLEncoder.encode(itemName, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
