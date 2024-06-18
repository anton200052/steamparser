package me.vasylkov.steamparser.selenium.configuration;

import jakarta.annotation.PreDestroy;
import lombok.Data;
import org.openqa.selenium.Proxy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "selenium")
public class SeleniumProperties {

    private String profilePath;
    private String driverPath;
    private String extensionPath;
    private int driverTimeout;
    private String browser;
    private List<String> proxyList;
    private boolean proxiesEnabled;
    private int proxiesBlockingTime;
}
