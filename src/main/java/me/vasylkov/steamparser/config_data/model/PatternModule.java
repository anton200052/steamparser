package me.vasylkov.steamparser.config_data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PatternModule extends Module {
    List<Integer> patternList;

    @Override
    public boolean isValid()
    {
        return !hasAnyRequiredFieldNull();
    }

    private boolean hasAnyRequiredFieldNull()
    {
        return patternList == null || super.getEnabled() == null;
    }
}
