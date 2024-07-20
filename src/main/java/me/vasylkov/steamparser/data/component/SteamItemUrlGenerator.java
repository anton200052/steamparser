package me.vasylkov.steamparser.data.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.selenium.configuration.SeleniumProperties;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SteamItemUrlGenerator implements UrlGenerator
{
    private final SeleniumProperties parsingProperties;

    public String generateListingsUrl(String itemName)
    {
        return "https://steamcommunity.com/market/listings/730/" + encodeItem(itemName) + "?l=english";
    }

    public String generatePriceOverviewApiUrl(String itemName) //192.168.0.107
    {
        return "http://localhost:8080/api/items/single" + "?hashName=" + itemName + "&currencyCode=" + parsingProperties.getSteamCurrencyCode();
    }

    private String encodeItem(String itemName)
    {
        return URLEncoder.encode(itemName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }
}
