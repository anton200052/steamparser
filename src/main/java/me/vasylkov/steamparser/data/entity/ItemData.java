package me.vasylkov.steamparser.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemData
{
    private String hashName;
    private Double medianPrice;
    private String listingsUrl;
    private String apiUrl;
    private Double maximumItemMarkup;
    private StickersModule stickersModule;


    public boolean isValid()
    {
        return !hasAnyFieldNull() && (stickersModule == null || stickersModule.isValid());
    }

    private boolean hasAnyFieldNull()
    {
        return hashName == null || medianPrice == null || listingsUrl == null || apiUrl == null || maximumItemMarkup == null;
    }
}
