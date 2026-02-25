package me.vasylkov.steamparser.parsing.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.vasylkov.steamparser.parsing.enums.ProfitableListingType;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ListingWithStickersMarkup extends ProfitableListing
{
    private Double totalStickersPrice;
    private Double priceWithStickersMarkup;
    private Double stickersMarkupPercentage;

        public ListingWithStickersMarkup(String listingId, String hashName, double price, List<Sticker> stickers, BigDecimal floatValue, int pattern, String imgUrl, Double totalStickersPrice, Double priceWithStickersMarkup, Double stickersMarkupPercentage) {
        super(listingId, hashName, price, stickers, floatValue, pattern, imgUrl, ProfitableListingType.STICKERS);
        this.totalStickersPrice = totalStickersPrice;
        this.priceWithStickersMarkup = priceWithStickersMarkup;
        this.stickersMarkupPercentage = stickersMarkupPercentage;
    }
}
