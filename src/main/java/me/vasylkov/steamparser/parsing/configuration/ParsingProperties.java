package me.vasylkov.steamparser.parsing.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "parsing")
public class ParsingProperties
{
    private String itemsFilePath;
    private String currencyCode;
    private int elementsWaitingDuration;
    private int pageChangingDuration;
    private boolean cycling;
    private int threads;
}
