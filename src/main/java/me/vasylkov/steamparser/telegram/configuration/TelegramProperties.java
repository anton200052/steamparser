package me.vasylkov.steamparser.telegram.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram")
@Data
public class TelegramProperties
{
    private String botToken; // Связывается с telegram.bot.token
}
