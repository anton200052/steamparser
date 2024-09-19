package me.vasylkov.steamparser.data.component;

public interface UrlGenerator
{
    String generateListingsUrl(String itemName);
    String generatePriceOverviewApiUrl(String itemName);
    String generateRenderUrl(String itemName, int start, int count);
    String generateCSFloatUrl(String url);
}
