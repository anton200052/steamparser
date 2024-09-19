package me.vasylkov.steamparser.parsing.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.data.entity.StickersModule;
import me.vasylkov.steamparser.parsing.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SteamPageAnalyser implements PageAnalyser
{
    private final SteamPriceCalculator steamPriceCalculator;

    @Override
    public AnalysingResult analysePage(Page page, Item item)
    {
        List<ListingWithStickersMarkup> profitableSteamListings = getProfitableListingsIfPresent(page, item);
        return new SteamAnalysingResult(profitableSteamListings);
    }

    private List<ListingWithStickersMarkup> getProfitableListingsIfPresent(Page page, Item item)
    {
        List<ListingWithStickersMarkup> profitableListings = new ArrayList<>();
        for (Listing listing : page.getListings())
        {
            if (item.getStickersModule() != null && item.getStickersModule().isValid())
            {
                analyseStickers(listing, item, profitableListings);
            }
        }
        return profitableListings;
    }

    private void analyseStickers(Listing listing, Item item, List<ListingWithStickersMarkup> profitableListings)
    {
        StickersModule stickersModule = item.getStickersModule();
        double totalStickersPrice = steamPriceCalculator.calculateTotalStickersPrice(listing.getStickers());
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

        double averageStickersMarkup = steamPriceCalculator.calculateAverageStickersMarkup(totalStickersPrice, averageItemPrice);
        double itemMarkup = steamPriceCalculator.calculateItemMarkupByStickers(averageStickersMarkup, currentItemPrice);

        profitableListings.add(new SteamListingWithStickersMarkup(listing.getHashName(), listing.getPrice(), listing.getStickers(), listing.getImgUrl(), totalStickersPrice, averageStickersMarkup, itemMarkup));
    }


    private boolean isItemPriceExceedingAverage(double averageItemPrice, double currentItemPrice)
    {
        return currentItemPrice > averageItemPrice;
    }
}
