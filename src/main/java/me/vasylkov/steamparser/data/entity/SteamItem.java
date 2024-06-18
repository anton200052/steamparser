package me.vasylkov.steamparser.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

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
    private boolean available = true;

    @Override
    public boolean isValid()
    {
        return !hasAnyFieldNull() && (stickersModule == null || stickersModule.isValid());
    }

    private boolean hasAnyFieldNull()
    {
        return hashName == null || averagePrice == null || listingsUrl == null || maximalItemMarkupPercentage == null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SteamItem steamItem = (SteamItem) o;
        return available == steamItem.available && Objects.equals(hashName, steamItem.hashName) && Objects.equals(averagePrice, steamItem.averagePrice) && Objects.equals(listingsUrl, steamItem.listingsUrl) && Objects.equals(maximalItemMarkupPercentage, steamItem.maximalItemMarkupPercentage) && Objects.equals(stickersModule, steamItem.stickersModule);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hashName, averagePrice, listingsUrl, maximalItemMarkupPercentage, stickersModule, available);
    }
}