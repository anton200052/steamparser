package me.vasylkov.steamparser.parsing.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "steamrender")
public class SteamRenderProperties {
    private int pageChangingDuration;
}
