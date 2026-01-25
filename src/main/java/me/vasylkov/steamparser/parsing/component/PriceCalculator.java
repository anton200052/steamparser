package me.vasylkov.steamparser.parsing.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.model.Sticker;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PriceCalculator
{
    public double calculateTotalStickersPrice(List<Sticker> stickers)
    {
        double totalStickersPrice = 0.0;
        for (Sticker sticker : stickers)
        {
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
