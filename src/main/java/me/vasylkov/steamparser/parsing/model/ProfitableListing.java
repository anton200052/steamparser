package me.vasylkov.steamparser.parsing.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.vasylkov.steamparser.parsing.enums.ProfitableListingType;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProfitableListing extends Listing {
    private ProfitableListingType profitableListingType;

    public ProfitableListing(String listingId, String hashName, double price, List<Sticker> stickers, double floatValue, int pattern, String imgUrl, ProfitableListingType profitableListingType) {
        super(listingId, hashName, price, stickers, floatValue, pattern, imgUrl);
        this.profitableListingType = profitableListingType;
    }
}
