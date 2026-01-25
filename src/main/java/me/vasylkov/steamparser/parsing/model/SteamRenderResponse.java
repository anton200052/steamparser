package me.vasylkov.steamparser.parsing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamRenderResponse
{
    private boolean success;
    private int start;
    @JsonProperty("pagesize")
    private int pageSize;
    @JsonProperty("total_count")
    private int totalCount;
    @JsonProperty("listinginfo")
    private Map<String, SteamRenderListing> listingInfoMap;
    @JsonProperty("assets")
    private Map<String, Map<String, Map<String, SteamAsset>>> assets;

    public SteamAsset getAssetBySteamRenderListing(SteamRenderListing steamRenderListing) {
        String appId = String.valueOf(steamRenderListing.getAsset().getAppId());
        String contextId = steamRenderListing.getAsset().getContextId();

        return assets.get(appId).get(contextId).get(steamRenderListing.getAsset().getId());
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SteamRenderListing
    {
        @JsonProperty("listingid")
        private String listingId;

        @JsonProperty("price")
        private int price;

        @JsonProperty("fee")
        private int fee;

        @JsonProperty("converted_price")
        private int convertedPrice;

        @JsonProperty("converted_fee")
        private int convertedFee;

        @JsonProperty("asset")
        private ListingAsset asset;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ListingAsset
        {
            @JsonProperty("appid")
            private int appId;

            @JsonProperty("contextid")
            private String contextId;

            @JsonProperty("id")
            private String id;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SteamAsset
    {
        @JsonProperty("icon_url")
        private String iconUrl;

        @JsonProperty("descriptions")
        private List<Description> descriptions;

        @JsonProperty("asset_properties")
        private List<AssetProperty> assetProperties;

        public List<Sticker> getStickers() {
            if (descriptions == null) return Collections.emptyList();

            return descriptions.stream()
                    .filter(d -> "sticker_info".equals(d.getName()))
                    .findFirst()
                    .map(d -> {
                        Document doc = Jsoup.parse(d.getValue());
                        return doc.select("img").stream()
                                .map(img -> new Sticker(img.attr("title").replace("Sticker: ", "")))
                                .collect(Collectors.toList());
                    })
                    .orElse(Collections.emptyList());
        }

        public Keychain getKeychain() {
            if (descriptions == null) return null;

            return descriptions.stream()
                    .filter(d -> "keychain_info".equals(d.getName()))
                    .findFirst()
                    .map(d -> {
                        Document doc = Jsoup.parse(d.getValue());
                        Element img = doc.selectFirst("img");
                        if (img != null) {
                            String name = img.attr("title").replace("Charm: ", "");
                            return new Keychain(name);
                        }
                        return null;
                    })
                    .orElse(null);
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Description
        {
            @JsonProperty("name")
            private String name;
            @JsonProperty("value")
            private String value;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class AssetProperty
        {
            @JsonProperty("propertyid")
            private Integer propertyId;
            @JsonProperty("float_value")
            private String floatValue;
            @JsonProperty("int_value")
            private String intValue;
            @JsonProperty("string_value")
            private String stringValue;
        }
    }
}