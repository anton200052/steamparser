package me.vasylkov.steamparser.config_data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StickersModule extends Module
{
    private Double minimalStickersPrice;

    @Override
    public boolean isValid()
    {
        return !hasAnyRequiredFieldNull();
    }

    private boolean hasAnyRequiredFieldNull()
    {
        return minimalStickersPrice == null || super.getEnabled() == null;
    }
}
