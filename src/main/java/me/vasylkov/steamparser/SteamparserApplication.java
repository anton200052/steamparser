package me.vasylkov.steamparser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SteamparserApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(SteamparserApplication.class, args);
    }

}
