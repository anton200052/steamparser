package me.vasylkov.steamparser.price_api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "priceapi")
public class PriceApiProperties
{
    private int itemPriceFetcherDuration;
}
