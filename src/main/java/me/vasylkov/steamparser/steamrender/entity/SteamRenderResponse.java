package me.vasylkov.steamparser.steamrender.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

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
    private Map<String, SteamRenderListing> listingInfo;

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

        @JsonProperty("publisher_fee_app")
        private int publisherFeeApp;

        @JsonProperty("publisher_fee_percent")
        private String publisherFeePercent;

        @JsonProperty("currencyid")
        private int currencyId;

        @JsonProperty("steam_fee")
        private int steamFee;

        @JsonProperty("publisher_fee")
        private int publisherFee;

        @JsonProperty("converted_price")
        private int convertedPrice;

        @JsonProperty("converted_fee")
        private int convertedFee;

        @JsonProperty("converted_currencyid")
        private int convertedCurrencyId;

        @JsonProperty("converted_steam_fee")
        private int convertedSteamFee;

        @JsonProperty("converted_publisher_fee")
        private int convertedPublisherFee;

        @JsonProperty("converted_price_per_unit")
        private int convertedPricePerUnit;

        @JsonProperty("converted_fee_per_unit")
        private int convertedFeePerUnit;

        @JsonProperty("converted_steam_fee_per_unit")
        private int convertedSteamFeePerUnit;

        @JsonProperty("converted_publisher_fee_per_unit")
        private int convertedPublisherFeePerUnit;

        @JsonProperty("asset")
        private Asset asset;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Asset
        {

            @JsonProperty("currency")
            private int currency;

            @JsonProperty("appid")
            private int appId;

            @JsonProperty("contextid")
            private String contextId;

            @JsonProperty("id")
            private String id;

            @JsonProperty("amount")
            private String amount;

            @JsonProperty("market_actions")
            private List<MarketAction> marketActions;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class MarketAction
            {

                @JsonProperty("link")
                private String link;

                @JsonProperty("name")
                private String name;

                public String resolveLink(String listingId, String assetId)
                {
                    return link.replace("%listingid%", listingId).replace("%assetid%", assetId);
                }
            }
        }
    }
}
