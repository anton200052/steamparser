package me.vasylkov.steamparser.parsing.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.model.*;
import me.vasylkov.steamparser.parsing.model.SteamRenderResponse;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SteamPageFetcher
{
    private final SteamRenderResponseFetcher steamRenderResponseFetcher;

    public Page fetchSteamPage(CloseableHttpClient closeableHttpClient, String itemName, int humanPageNum)
    {
        int zeroBasedPageNum = humanPageNum - 1;

        SteamRenderResponse steamRenderResponse = steamRenderResponseFetcher.fetchSteamRenderPageByItemName(closeableHttpClient, itemName, zeroBasedPageNum);
        int maxHumanPage = (steamRenderResponse.getTotalCount() + 9) / 10;
        List<Listing> listings = new ArrayList<>();

        for (Map.Entry<String, SteamRenderResponse.SteamRenderListing> steamRenderListingEntry : steamRenderResponse.getListingInfoMap().entrySet())
        {
            SteamRenderResponse.SteamRenderListing steamRenderListing = steamRenderListingEntry.getValue();
            String listingId = steamRenderListing.getListingId();

            SteamRenderResponse.SteamAsset steamAsset = steamRenderResponse.getAssetBySteamRenderListing(steamRenderListing);
            List<Sticker> stickers = steamAsset.getStickers();
            String imgUrl = "https://community.akamai.steamstatic.com/economy/image/" + steamAsset.getIconUrl();
            Integer pattern = steamAsset.getAssetProperties().stream()
                    .filter(p -> p.getPropertyId() == 1)
                    .findFirst()
                    .map(SteamRenderResponse.SteamAsset.AssetProperty::getIntValue)
                    .orElse(0);
            BigDecimal floatValue = steamAsset.getAssetProperties().stream()
                    .filter(p -> p.getPropertyId() == 2)
                    .findFirst()
                    .map(SteamRenderResponse.SteamAsset.AssetProperty::getFloatValue)
                    .orElse(BigDecimal.valueOf(0));

            double price = convertToNormalPrice(steamRenderListing.getConvertedPrice(), steamRenderListing.getConvertedFee());
            if (price == 0.0) {
                continue;
            }

            Listing listing = new Listing(listingId, itemName, price, stickers, floatValue, pattern, imgUrl);
            listings.add(listing);
        }

        return new Page(listings, humanPageNum, maxHumanPage);
    }

    public double convertToNormalPrice(int convertedPrice, int convertedFee)
    {
        if (convertedPrice != 0 && convertedFee != 0)
        {
            return (convertedPrice / 100.0) + (convertedFee / 100.0);
        }
        return 0.0;
    }

}
