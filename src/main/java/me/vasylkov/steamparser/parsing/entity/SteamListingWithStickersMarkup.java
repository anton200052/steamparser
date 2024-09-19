package me.vasylkov.steamparser.parsing.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SteamListingWithStickersMarkup extends ListingWithStickersMarkup
{
    public SteamListingWithStickersMarkup()
    {
    }

    public SteamListingWithStickersMarkup(String hashName, double price, List<Sticker> stickers, String imgUrl, Double totalStickersPrice, Double priceWithStickersMarkup, Double stickersMarkupPercentage)
    {
        super(hashName, price, stickers, imgUrl, totalStickersPrice, priceWithStickersMarkup, stickersMarkupPercentage);
    }
}
