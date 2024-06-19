package me.vasylkov.steamparser.price_api.component;

public interface ItemPriceFetcher
{
    double fetchItemAveragePrice(String priceApiUrl);
}
