package me.vasylkov.steamparser.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "priceapi")
public class PriceApiProperties
{
    private int itemPriceFetcherDelay;
    private String url;
}
