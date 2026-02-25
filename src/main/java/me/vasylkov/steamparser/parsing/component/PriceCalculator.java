package me.vasylkov.steamparser.parsing.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.config_data.component.UrlGenerator;
import me.vasylkov.steamparser.parsing.model.Sticker;
import me.vasylkov.steamparser.price_api.component.ItemPriceFetcher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PriceCalculator
{
    private final ItemPriceFetcher itemPriceFetcher;
    private final UrlGenerator urlGenerator;

    public double calculateTotalStickersPrice(List<Sticker> stickers)
    {
        double totalStickersPrice = 0.0;
        for (Sticker sticker : stickers)
        {
            if (sticker.getPrice() == 0.0) {
                String priceApiUrl = urlGenerator.generatePriceOverviewApiUrl("Sticker | " + sticker.getHashName());
                double fetchedPrice = itemPriceFetcher.fetchItemAveragePrice(priceApiUrl);
                sticker.setPrice(fetchedPrice);
            }
            totalStickersPrice += sticker.getPrice();
        }
        return totalStickersPrice;
    }

    public double calculateItemMarkupByStickers(double averageStickersMarkup, double currentItemPrice)
    {
        return ((averageStickersMarkup - currentItemPrice) / currentItemPrice) * 100;
    }

    public double calculateAverageStickersMarkup(double totalStickersPrice, double averageItemPrice)
    {
        double adjustedStickersPrice = totalStickersPrice * 0.05;
        return adjustedStickersPrice + averageItemPrice;
    }
}
