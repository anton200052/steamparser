package me.vasylkov.steamparser.parsing.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "parsing")
public class ParsingProperties
{
    private boolean cycle;
    private int threads;
    private boolean enableProxy;
    private int proxyBlockingTime;
    private List<String> proxyList;
}
