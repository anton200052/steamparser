package me.vasylkov.steamparser.parsing.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.csfloat.component.CSFloatApiResponseFetcher;
import me.vasylkov.steamparser.csfloat.model.CSFloatApiResponse;
import me.vasylkov.steamparser.parsing.model.*;
import me.vasylkov.steamparser.parsing.configuration.SteamRenderProperties;
import me.vasylkov.steamparser.parsing.model.SteamRenderResponse;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

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
    private final SteamRenderProperties steamRenderProperties;
    private final Logger logger;

    public Page fetchSteamPage(CloseableHttpClient closeableHttpClient, String itemName, int humanPageNum)
    {
        waitBeforeFetching();

        int zeroBasedPageNum = humanPageNum - 1;

        SteamRenderResponse steamRenderResponse = steamRenderResponseFetcher.fetchSteamRenderPageByItemName(closeableHttpClient, itemName, zeroBasedPageNum);
        int maxHumanPage = (steamRenderResponse.getTotalCount() + 9) / 10;
        List<Listing> listings = new ArrayList<>();

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

            CSFloatApiResponse.CSFloatItemInfo itemInfo = csFloatApiResponse.getItemInfo();

            List<Sticker> stickers = steamPageDataConverter.convertCSFloatStickersListToSteamStickersList(itemInfo.getStickers());
            int pattern = itemInfo.getPaintSeed();
            double floatVal = itemInfo.getFloatValue();
            String imgUrl = itemInfo.getImageUrl();

            Listing listing = new Listing(listingId, itemName, price, stickers, floatVal, pattern, imgUrl);
            listings.add(listing);
        }

        return new Page(listings, humanPageNum, maxHumanPage);
    }

    private void waitBeforeFetching() {
        try {
            Thread.sleep(steamRenderProperties.getPageChangingDuration() * 1000L);
        }
        catch (InterruptedException e) {
            logger.error("Ошибка при ожидании перед сменой страницы", e);
        }
    }
}
