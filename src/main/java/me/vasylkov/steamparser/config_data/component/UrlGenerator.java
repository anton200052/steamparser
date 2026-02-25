package me.vasylkov.steamparser.config_data.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.properties.SteamProperties;
import me.vasylkov.steamparser.properties.PriceApiProperties;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class UrlGenerator
{
    private final SteamProperties steamProperties;
    private final PriceApiProperties priceApiProperties;

    public String generateRenderUrl(String itemName, int start, int count)
    {
        return String.format("https://steamcommunity.com/market/listings/730/%s/render/?query=&start=%d&count=%d&currency=%d", encodeItem(itemName), start, count, steamProperties.getCurrencyCode().getCode());
    }
    
    public String generatePriceOverviewApiUrl(String itemName)
    {
        return String.format(priceApiProperties.getUrl() + "?hashName=%s&currencyCode=%s", itemName, steamProperties.getCurrencyCode());
    }

    private String encodeItem(String itemName)
    {
        return URLEncoder.encode(itemName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }
}
