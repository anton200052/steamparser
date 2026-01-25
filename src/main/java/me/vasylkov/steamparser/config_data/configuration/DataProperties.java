package me.vasylkov.steamparser.config_data.configuration;

import lombok.Data;
import me.vasylkov.steamparser.config_data.model.Item;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "data")
@Data
public class DataProperties
{
    private List<Item> items;
}
