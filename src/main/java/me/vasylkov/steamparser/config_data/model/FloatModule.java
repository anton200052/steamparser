package me.vasylkov.steamparser.config_data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FloatModule extends Module {
    private BigDecimal minFloat;
    private BigDecimal maxFloat;

    @Override
    public boolean isValid()
    {
        return !hasAnyRequiredFieldNull();
    }

    private boolean hasAnyRequiredFieldNull()
    {
        return minFloat == null || maxFloat == null || super.getEnabled() == null;
    }
}
