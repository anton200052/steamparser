package me.vasylkov.steamparser.parsing.component;

import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.data.entity.StickersModule;
import me.vasylkov.steamparser.parsing.entity.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
public class SteamPageAnalyser implements PageAnalyser
{
    @Override
    public AnalysingResult analysePage(Page page, Item item)
    {
        List<Listing> profitableSteamListings = getProfitableListingsIfPresent(page, item);
        boolean priceExceedsMaxItemMarkup = hasListingsPriceThreshold(page, item);
        return new SteamAnalysingResult(profitableSteamListings, priceExceedsMaxItemMarkup);
    }

    private List<Listing> getProfitableListingsIfPresent(Page page, Item item)
    {
        List<Listing> profitableListings = new ArrayList<>();
        for (Listing listing : page.getListings())
        {
            if (listing.getPrice() > calculatePriceThreshold(item))
            {
                break;
            }

            if (item.getStickersModule() != null && item.getStickersModule().isValid())
            {
                analyseStickers(listing, item, profitableListings);
            }
        }
        return profitableListings;
    }

    private void analyseStickers(Listing listing, Item item, List<Listing> profitableListings)
    {
        StickersModule stickersModule = item.getStickersModule();
        double totalStickersPrice = calculateTotalStickersPrice(listing.getStickers(), stickersModule.getMinimalStickerPrice());
        double currentItemPrice = listing.getPrice();
        double averageItemPrice = item.getAveragePrice();

        if (isItemPriceNotExceedingAverage(averageItemPrice, currentItemPrice))
        {
            averageItemPrice = currentItemPrice;
        }

        double averageStickersMarkup = calculateAverageStickersMarkup(totalStickersPrice, averageItemPrice);
        double itemMarkup = calculateItemMarkupByStickers(averageStickersMarkup, currentItemPrice);

        if (itemMarkup >= stickersModule.getMinimalMarkupPercentage())
        {
            listing.setPriceWithStickersMarkup(averageStickersMarkup);
            listing.setStickersMarkupPercentage(itemMarkup);
            profitableListings.add(listing);
        }
    }

    private double calculateTotalStickersPrice(List<Sticker> steamStickers, double minimalStickersPrice)
    {
        double totalStickersPrice = 0.0;
        for (Sticker steamSticker : steamStickers)
        {
            double price = steamSticker.getPrice();
            if (price >= minimalStickersPrice)
            {
                totalStickersPrice += price;
            }
        }
        return totalStickersPrice;
    }

    private double calculateItemMarkupByStickers(double averageStickersMarkup, double currentItemPrice)
    {
        return ((averageStickersMarkup - currentItemPrice) / currentItemPrice) * 100;
    }

    private double calculateAverageStickersMarkup(double totalStickersPrice, double averageItemPrice)
    {
        double adjustedStickersPrice = totalStickersPrice * 0.05;
        return adjustedStickersPrice + averageItemPrice;
    }


    private boolean hasListingsPriceThreshold(Page page, Item item)
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

    private boolean isItemPriceNotExceedingAverage(double averageItemPrice, double currentItemPrice)
    {
        return currentItemPrice <= averageItemPrice;
    }
}
