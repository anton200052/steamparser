package me.vasylkov.steamparser.data.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ItemUrlGenerator
{
    private final ParsingProperties parsingProperties;

    public String generateListingsUrl(String itemName)
    {
        return "https://steamcommunity.com/market/listings/730/" + itemName + "?l=english";
    }

    public String generatePriceOverviewApiUrl(String itemName)
    {
        return "http://localhost:8080/api/items/singleItem" + "?hashName=" + itemName + "&currencyCode=" + parsingProperties.getCurrencyCode();
    }
}
