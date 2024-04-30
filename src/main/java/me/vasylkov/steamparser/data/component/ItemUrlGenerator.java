package me.vasylkov.steamparser.data.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ItemUrlGenerator
{
    private final ParsingProperties parsingProperties;

    public String generateSteamListingsUrl(String itemName)
    {
        return "https://steamcommunity.com/market/listings/730/" + encodeItem(itemName) + "?l=english";
    }

    public String generatePriceOverviewApiUrl(String itemName)
    {
        return "http://192.168.0.107:8080/api/items/singleItem" + "?hashName=" + itemName + "&currencyCode=" + parsingProperties.getCurrencyCode();
    }

    private String encodeItem(String itemName)
    {
        return URLEncoder.encode(itemName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }
}
