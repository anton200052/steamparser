package me.vasylkov.steamparser.parsing.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class ListingWithStickersMarkup extends SteamListing
{
    private Double totalStickersPrice;
    private Double priceWithStickersMarkup;
    private Double stickersMarkupPercentage;

    public ListingWithStickersMarkup(String hashName, double price, List<Sticker> stickers, String imgUrl, Double totalStickersPrice, Double priceWithStickersMarkup, Double stickersMarkupPercentage)
    {
        super(hashName, price, stickers, imgUrl);
        this.totalStickersPrice = totalStickersPrice;
        this.priceWithStickersMarkup = priceWithStickersMarkup;
        this.stickersMarkupPercentage = stickersMarkupPercentage;
    }
}
