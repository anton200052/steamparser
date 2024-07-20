package me.vasylkov.steamparser.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StickersModule implements Module
{
    private Boolean enabled;
    private Double minimalStickersPrice;

    @Override
    public boolean isValid()
    {
        return !hasAnyRequiredFieldNull();
    }

    private boolean hasAnyRequiredFieldNull()
    {
        return minimalStickersPrice == null || enabled == null;
    }
}
