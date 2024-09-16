package me.vasylkov.steamparser.parsing.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CSFloatApiResponse
{
    @JsonProperty("iteminfo")
    public CSFloatItemInfo itemInfo;

    @Data
    public static class CSFloatItemInfo
    {
        @JsonProperty("origin")
        private int origin;

        @JsonProperty("quality")
        private int quality;

        @JsonProperty("rarity")
        private int rarity;

        @JsonProperty("paintseed")
        private int paintSeed;

        @JsonProperty("defindex")
        private int defIndex;

        @JsonProperty("paintindex")
        private int paintIndex;

        @JsonProperty("stickers")
        private List<CSFloatSticker> stickers;

        @JsonProperty("floatvalue")
        private double floatValue;

        @JsonProperty("imageurl")
        private String imageUrl;

        @JsonProperty("min")
        private double min;

        @JsonProperty("max")
        private double max;

        @JsonProperty("weapon_type")
        private String weaponType;

        @JsonProperty("item_name")
        private String itemName;

        @JsonProperty("rarity_name")
        private String rarityName;

        @JsonProperty("quality_name")
        private String qualityName;

        @JsonProperty("wear_name")
        private String wearName;

        @JsonProperty("full_item_name")
        private String fullItemName;

        @JsonProperty("s")
        private String s;

        @JsonProperty("a")
        private String a;

        @JsonProperty("d")
        private String d;

        @JsonProperty("m")
        private String m;

    }

    @Data
    public static class CSFloatSticker
    {
        @JsonProperty("stickerId")
        private int stickerId;

        @JsonProperty("slot")
        private int slot;

        @JsonProperty("wear")
        private double wear;

        @JsonProperty("codename")
        private String codeName;

        @JsonProperty("material")
        private String material;

        @JsonProperty("name")
        private String name;
    }
}
