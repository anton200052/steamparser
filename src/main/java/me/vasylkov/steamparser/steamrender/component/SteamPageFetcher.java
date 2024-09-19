package me.vasylkov.steamparser.steamrender.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.csfloat.component.CSFloatApiResponseFetcher;
import me.vasylkov.steamparser.csfloat.entity.CSFloatApiResponse;
import me.vasylkov.steamparser.parsing.entity.*;
import me.vasylkov.steamparser.price_api.component.ItemPriceFetcher;
import me.vasylkov.steamparser.steamrender.entity.SteamRenderResponse;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SteamPageFetcher
{
    private final SteamRenderResponseFetcher steamRenderResponseFetcher;
    private final CSFloatApiResponseFetcher csFloatApiResponseFetcher;
    private final SteamPageDataConverter steamPageDataConverter;

    public Page fetchSteamPage(CloseableHttpClient closeableHttpClient, String itemName, int pageNumber)
    {
        SteamRenderResponse steamRenderResponse = steamRenderResponseFetcher.fetchSteamRenderPageByItemName(closeableHttpClient, itemName, pageNumber);
        int maxPage = steamRenderResponse.getTotalCount() / 10;
        List<Listing> listings = new ArrayList<>();

        System.out.println(steamRenderResponse.getListingInfo());
        for (Map.Entry<String, SteamRenderResponse.SteamRenderListing> steamRenderListingEntry : steamRenderResponse.getListingInfo().entrySet())
        {
            SteamRenderResponse.SteamRenderListing steamRenderListing = steamRenderListingEntry.getValue();
            String listingId = steamRenderListing.getListingId();
            String assetId = steamRenderListing.getAsset().getId();
            CSFloatApiResponse csFloatApiResponse = csFloatApiResponseFetcher.fetchCSFloatResponse(steamRenderListing.getAsset().getMarketActions().get(0).resolveLink(listingId, assetId));

            double price = steamPageDataConverter.convertToNormalPrice(steamRenderListing.getConvertedPrice(), steamRenderListing.getConvertedFee());
            if (price == 0.0) {
                continue;
            }
            List<Sticker> stickers = steamPageDataConverter.convertCSFloatStickersListToSteamStickersList(csFloatApiResponse.getItemInfo().getStickers());
            String imgUrl = csFloatApiResponse.getItemInfo().getImageUrl();

            Listing listing = new SteamListing(itemName, price, stickers, imgUrl);
            listings.add(listing);
        }

        return new SteamPage(listings, pageNumber, maxPage);
    }
}
