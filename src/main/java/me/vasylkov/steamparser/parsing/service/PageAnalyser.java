package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.data.entity.ItemData;
import me.vasylkov.steamparser.data.entity.StickersModule;
import me.vasylkov.steamparser.parsing.entity.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PageAnalyser
{
    public AnalysingResult analysePage(Page page, ItemData itemData)
    {
        List<Listing> profitableListings = getProfitableListingsIfPresent(page, itemData);
        boolean priceExceedsMaxItemMarkup = hasListingPriceThatExceedsMaxMarkup(page, itemData);
        return new AnalysingResult(profitableListings, priceExceedsMaxItemMarkup);
    }

    private List<Listing> getProfitableListingsIfPresent(Page page, ItemData itemData)
    {
        List<Listing> profitableListings = new ArrayList<>();
        for (Listing listing : page.getListings())
        {
            if (listing.getPrice() > calculatePriceThreshold(itemData))
            {
                break;
            }

            if (itemData.getStickersModule() != null && itemData.getStickersModule().isValid())
            {
                analyseStickers(listing, itemData, profitableListings);
            }
        }
        return profitableListings;
    }

    private void analyseStickers(Listing listing, ItemData itemData, List<Listing> profitableListings)
    {
        StickersModule stickersModule = itemData.getStickersModule();
        double totalStickersPrice = 0.0;
        for (Sticker sticker : listing.getStickers())
        {
            double price = sticker.getPrice();
            if (price >= stickersModule.getMinimalStickerPrice())
            {
                totalStickersPrice += price;
            }
        }
        double adjustedStickersPrice = totalStickersPrice * 0.05;
        double priceWithMarkup = adjustedStickersPrice + itemData.getMedianPrice();
        double percentageMarkup = 0.0;
        if (itemData.getMedianPrice() != 0.0)
        {
            percentageMarkup = ((priceWithMarkup - itemData.getMedianPrice()) / itemData.getMedianPrice()) * 100;
        }

        if (percentageMarkup >= stickersModule.getMinimalMarkup())
        {
            listing.setPriceWithStickersMarkup(priceWithMarkup);
            listing.setStickersMarkupPercentage(percentageMarkup);
            profitableListings.add(listing);
        }
    }

    private boolean hasListingPriceThatExceedsMaxMarkup(Page page, ItemData itemData)
    {
        double priceThreshold = calculatePriceThreshold(itemData);
        for (Listing listing : page.getListings())
        {
            if (listing.getPrice() > priceThreshold)
            {
                return true;
            }
        }
        return false;
    }


    private double calculatePriceThreshold(ItemData itemData)
    {
        double medianPrice = itemData.getMedianPrice();
        double maximumItemMarkup = itemData.getMaximumItemMarkup();
        return medianPrice * (1 + maximumItemMarkup / 100.0);
    }
}
