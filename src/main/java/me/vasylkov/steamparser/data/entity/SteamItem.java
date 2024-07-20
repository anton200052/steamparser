package me.vasylkov.steamparser.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamItem implements Item
{
    private String hashName;
    private Double averagePrice;
    private String listingsUrl;
    private Integer maximalPage;
    private StickersModule stickersModule;
    private Boolean available = true;

    @Override
    public boolean isValid()
    {
        return !hasAnyRequiredFieldNull() && isModulesValid();
    }

    private boolean hasAnyRequiredFieldNull()
    {
        return hashName == null || averagePrice == null || listingsUrl == null || maximalPage == null || available == null;
    }

    private boolean isModulesValid()
    {
        return stickersModule == null || stickersModule.isValid();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SteamItem steamItem = (SteamItem) o;
        return available == steamItem.available && Objects.equals(hashName, steamItem.hashName) && Objects.equals(averagePrice, steamItem.averagePrice) && Objects.equals(listingsUrl, steamItem.listingsUrl) && Objects.equals(maximalPage, steamItem.maximalPage) && Objects.equals(stickersModule, steamItem.stickersModule);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hashName, averagePrice, listingsUrl, maximalPage, stickersModule, available);
    }
}