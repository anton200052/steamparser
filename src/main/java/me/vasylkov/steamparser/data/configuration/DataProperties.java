package me.vasylkov.steamparser.data.configuration;

import lombok.Data;
import me.vasylkov.steamparser.data.entity.ItemData;
import me.vasylkov.steamparser.data.entity.StickersModule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "data")
public class DataProperties
{
    private List<ItemData> items;
}
