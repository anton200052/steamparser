package me.vasylkov.steamparser.parsing.configuration;

import lombok.Data;
import me.vasylkov.steamparser.parsing.enums.ParsingType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "steam")
public class SteamProperties
{
    private String currencyCode;
}
