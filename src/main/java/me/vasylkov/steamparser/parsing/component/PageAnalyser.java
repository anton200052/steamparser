package me.vasylkov.steamparser.parsing.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.config_data.model.FloatModule;
import me.vasylkov.steamparser.config_data.model.PatternModule;
import me.vasylkov.steamparser.config_data.model.PricedItem;
import me.vasylkov.steamparser.config_data.model.StickersModule;
import me.vasylkov.steamparser.parsing.enums.ProfitableListingType;
import me.vasylkov.steamparser.parsing.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PageAnalyser {
    private final PriceCalculator priceCalculator;
    private final BlacklistChecker blacklistChecker;

    public AnalysingResult analysePage(Page page, PricedItem item) {
        List<ProfitableListing> profitableSteamListings = getProfitableListingsIfPresent(page, item);
        return new AnalysingResult(profitableSteamListings);
    }

    private List<ProfitableListing> getProfitableListingsIfPresent(Page page, PricedItem item) {
        List<ProfitableListing> profitableListings = new ArrayList<>();
        for (Listing listing : page.getListings()) {
            if (item.isValid() && !blacklistChecker.isBlacklisted(listing.getListingId())) {

                StickersModule stickersModule = item.getStickersModule();
                if (stickersModule != null && stickersModule.getEnabled()) {
                    ProfitableListing profitableListing = getIfProfitableStickerListing(listing, item);
                    if (profitableListing != null) {
                        profitableListings.add(profitableListing);
                    }
                }

                FloatModule floatModule = item.getFloatModule();
                if (floatModule != null && floatModule.getEnabled()) {
                    ProfitableListing profitableListing = getIfProfitableFloatListing(listing, item);
                    if (profitableListing != null) {
                        profitableListings.add(profitableListing);
                    }
                }

                PatternModule patternModule = item.getPatternModule();
                if (patternModule != null && patternModule.getEnabled()) {
                    ProfitableListing profitableListing = getIfProfitablePatternListing(listing, item);
                    if (profitableListing != null) {
                        profitableListings.add(profitableListing);
                    }
                }
            }
        }
        return profitableListings;
    }

    private ProfitableListing getIfProfitableStickerListing(Listing listing, PricedItem item) {
        StickersModule stickersModule = item.getStickersModule();
        double totalStickersPrice = priceCalculator.calculateTotalStickersPrice(listing.getStickers());
        double priceWithStickersMarkup = priceCalculator.calculateAverageStickersMarkup(totalStickersPrice, item.getAveragePrice());
        double stickersMarkupPercent = priceCalculator.calculateItemMarkupByStickers(priceWithStickersMarkup, item.getAveragePrice());

        if (totalStickersPrice >= stickersModule.getMinimalStickersPrice()) {
            return new ListingWithStickersMarkup(listing.getListingId(), listing.getHashName(), listing.getPrice(), listing.getStickers(), listing.getFloatValue(), listing.getPattern(), listing.getImgUrl(), totalStickersPrice, priceWithStickersMarkup, stickersMarkupPercent);
        }
        return null;
    }

    private ProfitableListing getIfProfitableFloatListing(Listing listing, PricedItem item) {
        FloatModule floatModule = item.getFloatModule();
        BigDecimal floatVal = listing.getFloatValue();
        System.out.println(listing.getFloatValue());

        if (floatVal.compareTo(floatModule.getMinFloat()) >= 0 && floatVal.compareTo(floatModule.getMaxFloat()) <= 0) {
            return new ProfitableListing(listing.getListingId(), listing.getHashName(), listing.getPrice(), listing.getStickers(), listing.getFloatValue(), listing.getPattern(), listing.getImgUrl(), ProfitableListingType.FLOAT);
        }
        return null;
    }

    private ProfitableListing getIfProfitablePatternListing(Listing listing, PricedItem item) {
        PatternModule patternModule = item.getPatternModule();
        int pattern = listing.getPattern();

        if (patternModule.getPatternList().contains(pattern)) {
            return new ProfitableListing(listing.getListingId(), listing.getHashName(), listing.getPrice(), listing.getStickers(), listing.getFloatValue(), listing.getPattern(), listing.getImgUrl(), ProfitableListingType.PATTERN);
        }
        return null;
    }
}
