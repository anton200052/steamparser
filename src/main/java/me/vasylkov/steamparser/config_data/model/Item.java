package me.vasylkov.steamparser.config_data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item implements Validable
{
    private String hashName;
    private Integer maximalPage;
    private StickersModule stickersModule;
    private FloatModule floatModule;
    private PatternModule patternModule;

    @Override
    public boolean isValid() {
        return hashName != null
                && maximalPage != null
                && (stickersModule == null || stickersModule.isValid())
                && (floatModule == null || floatModule.isValid())
                && (patternModule == null || patternModule.isValid());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Item item = (Item) o;
        return Objects.equals(hashName, item.hashName) && Objects.equals(maximalPage, item.maximalPage) && Objects.equals(stickersModule, item.stickersModule) && Objects.equals(floatModule, item.floatModule) && Objects.equals(patternModule, item.patternModule);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hashName, maximalPage, stickersModule, floatModule, patternModule);
    }
}