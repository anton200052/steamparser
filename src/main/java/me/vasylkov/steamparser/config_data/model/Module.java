package me.vasylkov.steamparser.config_data.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Module implements Validable {
    private Boolean enabled;
}
