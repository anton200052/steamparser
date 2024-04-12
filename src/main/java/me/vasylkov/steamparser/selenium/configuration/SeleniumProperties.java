package me.vasylkov.steamparser.selenium.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "selenium")
public class SeleniumProperties {

    private String profilePath;
    private String driverPath;
    private String extensionPath;
    private int driverTimeout;
}
