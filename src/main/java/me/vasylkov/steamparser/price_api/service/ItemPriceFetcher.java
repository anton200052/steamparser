package me.vasylkov.steamparser.price_api.service;

import me.vasylkov.steamparser.data.entity.Item;

public interface ItemPriceFetcher
{
    double fetchItemAveragePrice(String priceApiUrl);
}
