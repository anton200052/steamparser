package me.vasylkov.steamparser.data.configuration;

import lombok.Data;
import me.vasylkov.steamparser.data.entity.SteamItem;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Data
@Configuration
@ConfigurationProperties(prefix = "data")
public class DataProperties
{
    private ConcurrentLinkedDeque<SteamItem> steamItems;
}
