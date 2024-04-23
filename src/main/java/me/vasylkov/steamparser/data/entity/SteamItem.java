package me.vasylkov.steamparser.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamItem implements Item
{
    private String hashName;
    private Double averagePrice;
    private String listingsUrl;
    private Double maximalItemMarkupPercentage;
    private StickersModule stickersModule;

    @Override
    public boolean isValid()
    {
        return !hasAnyFieldNull() && (stickersModule == null || stickersModule.isValid());
    }

    private boolean hasAnyFieldNull()
    {
        return hashName == null || averagePrice == null || listingsUrl == null || maximalItemMarkupPercentage == null;
    }
}