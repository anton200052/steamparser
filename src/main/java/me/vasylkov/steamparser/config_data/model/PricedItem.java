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

    public PricedItem(String hashName, Integer maximalPage, StickersModule stickersModule, Double averagePrice, Boolean available) {
        super(hashName, maximalPage, stickersModule, null, null);
        this.averagePrice = averagePrice;
        this.available = available;
    }
}
