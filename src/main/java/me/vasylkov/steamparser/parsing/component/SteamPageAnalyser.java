package me.vasylkov.steamparser.parsing.component;

import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.data.entity.StickersModule;
import me.vasylkov.steamparser.parsing.entity.AnalysingResult;
import me.vasylkov.steamparser.parsing.entity.Listing;
import me.vasylkov.steamparser.parsing.entity.Page;
import me.vasylkov.steamparser.parsing.entity.Sticker;
import me.vasylkov.steamparser.parsing.entity.SteamAnalysingResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SteamPageAnalyser implements PageAnalyser
{
    @Override
    public AnalysingResult analysePage(Page page, Item item)
    {
        List<Listing> profitableSteamListings = getProfitableListingsIfPresent(page, item);
        return new SteamAnalysingResult(profitableSteamListings);
    }

    private List<Listing> getProfitableListingsIfPresent(Page page, Item item)
    {
        List<Listing> profitableListings = new ArrayList<>();
        for (Listing listing : page.getListings())
        {
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
        double totalStickersPrice = calculateTotalStickersPrice(listing.getStickers());
        double currentItemPrice = listing.getPrice();
        double averageItemPrice = item.getAveragePrice();

        if (totalStickersPrice < stickersModule.getMinimalStickersPrice())
        {
            return;
        }

        if (!isItemPriceExceedingAverage(averageItemPrice, currentItemPrice))
        {
            averageItemPrice = currentItemPrice;
        }

        double averageStickersMarkup = calculateAverageStickersMarkup(totalStickersPrice, averageItemPrice);
        double itemMarkup = calculateItemMarkupByStickers(averageStickersMarkup, currentItemPrice);
        listing.setPriceWithStickersMarkup(averageStickersMarkup);
        listing.setStickersMarkupPercentage(itemMarkup);

        profitableListings.add(listing);
    }

    private double calculateTotalStickersPrice(List<Sticker> steamStickers)
    {
        double totalStickersPrice = 0.0;
        for (Sticker steamSticker : steamStickers)
        {
            double price = steamSticker.getPrice();
            totalStickersPrice += price;
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


    private boolean isItemPriceExceedingAverage(double averageItemPrice, double currentItemPrice)
    {
        return currentItemPrice > averageItemPrice;
    }
}
