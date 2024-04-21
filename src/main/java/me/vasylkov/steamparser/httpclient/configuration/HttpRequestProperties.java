package me.vasylkov.steamparser.httpclient.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "http")
public class HttpRequestProperties
{
    private int itemPriceGetterDuration;
}
