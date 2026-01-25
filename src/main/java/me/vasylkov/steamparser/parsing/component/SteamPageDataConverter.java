package me.vasylkov.steamparser.parsing.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.csfloat.model.CSFloatApiResponse;
import me.vasylkov.steamparser.config_data.component.UrlGenerator;
import me.vasylkov.steamparser.parsing.model.Sticker;
import me.vasylkov.steamparser.price_api.component.ItemPriceFetcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SteamPageDataConverter
{
    private final ItemPriceFetcher itemPriceFetcher;
    private final UrlGenerator urlGenerator;

    public double convertToNormalPrice(int convertedPrice, int convertedFee)
    {
        if (convertedPrice != 0 && convertedFee != 0)
        {
            return (convertedPrice / 100.0) + (convertedFee / 100.0);
        }
        return 0.0;
    }

    public List<Sticker> convertCSFloatStickersListToSteamStickersList(List<CSFloatApiResponse.CSFloatSticker> stickers)
    {
        List<Sticker> result = new ArrayList<Sticker>();
        if (stickers != null)
        {
            for (CSFloatApiResponse.CSFloatSticker csFloatSticker : stickers)
            {
                String name = csFloatSticker.getName();
                result.add(new Sticker(name, itemPriceFetcher.fetchItemAveragePrice(urlGenerator.generatePriceOverviewApiUrl("Sticker | " + name))));
            }
        }
        return result;
    }
}
