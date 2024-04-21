package me.vasylkov.steamparser.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StickersModule
{
    private Boolean enabled;
    private Double minimalMarkupPercentage;
    private Double minimalStickerPrice;

    public boolean isValid()
    {
        return (enabled == null || !enabled) || (enabled && !hasAnyFieldNull());
    }


    private boolean hasAnyFieldNull()
    {
        return minimalMarkupPercentage == null || minimalStickerPrice == null;
    }
}
