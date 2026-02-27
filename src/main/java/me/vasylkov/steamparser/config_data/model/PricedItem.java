package me.vasylkov.steamparser.config_data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PricedItem extends Item {
    private Double averagePrice;
    private Boolean available = true;

    public PricedItem(String hashName, Integer maximalPage, Double averagePrice, Boolean available, StickersModule stickersModule, FloatModule floatModule, PatternModule patternModule) {
        super(hashName, maximalPage, stickersModule, floatModule, patternModule);
        this.averagePrice = averagePrice;
        this.available = available;
    }
}
