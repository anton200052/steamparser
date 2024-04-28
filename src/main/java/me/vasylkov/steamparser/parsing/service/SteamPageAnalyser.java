package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.data.entity.SteamItem;
import me.vasylkov.steamparser.data.entity.StickersModule;
import me.vasylkov.steamparser.parsing.entity.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SteamPageAnalyser implements PageAnalyser
{
    public AnalysingResult analysePage(Page page, Item item)
    {
        SteamItem steamItem = (SteamItem) item;
        List<Listing> profitableListings = getProfitableListingsIfPresent(page, steamItem);
        boolean priceExceedsMaxItemMarkup = hasListingPriceThatExceedsMaxMarkup(page, steamItem);
        return new AnalysingResult(profitableListings, priceExceedsMaxItemMarkup);
    }

    private List<Listing> getProfitableListingsIfPresent(Page page, SteamItem steamItem)
    {
        List<Listing> profitableListings = new ArrayList<>();
        for (Listing listing : page.getListings())
        {
            if (listing.getPrice() > calculatePriceThreshold(steamItem))
            {
                break;
            }

            if (steamItem.getStickersModule() != null && steamItem.getStickersModule().isValid())
            {
                analyseStickers(listing, steamItem, profitableListings);
            }
        }
        return profitableListings;
    }

    private void analyseStickers(Listing listing, Item item, List<Listing> profitableListings)
    {
        StickersModule stickersModule = item.getStickersModule();
        double totalStickersPrice = 0.0;
        double currentPrice = listing.getPrice();
        for (Sticker sticker : listing.getStickers())
        {
            double price = sticker.getPrice();
            if (price >= stickersModule.getMinimalStickerPrice())
            {
                totalStickersPrice += price;
            }
        }
        double adjustedStickersPrice = totalStickersPrice * 0.05;
        double priceWithMarkup = adjustedStickersPrice + item.getAveragePrice();
        double percentageMarkup = 0.0;
        if (item.getAveragePrice() != 0.0)
        {
            percentageMarkup = ((priceWithMarkup - currentPrice) / currentPrice) * 100;
        }

        if (percentageMarkup >= stickersModule.getMinimalMarkupPercentage())
        {
            listing.setPriceWithStickersMarkup(priceWithMarkup);
            listing.setStickersMarkupPercentage(percentageMarkup);
            profitableListings.add(listing);
        }
    }

    private boolean hasListingPriceThatExceedsMaxMarkup(Page page, Item item)
    {
        double priceThreshold = calculatePriceThreshold(item);
        for (Listing listing : page.getListings())
        {
            if (listing.getPrice() > priceThreshold)
            {
                return true;
            }
        }
        return false;
    }


    private double calculatePriceThreshold(Item item)
    {
        double medianPrice = item.getAveragePrice();
        double maximumItemMarkup = item.getMaximalItemMarkupPercentage();
        return medianPrice * (1 + maximumItemMarkup / 100.0);
    }
}
