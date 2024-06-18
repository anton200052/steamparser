package me.vasylkov.steamparser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAsync(proxyTargetClass = true)
public class SteamparserApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SteamparserApplication.class, args);
    }
}
