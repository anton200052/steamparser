package me.vasylkov.steamparser.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "telegram")
@Data
public class TelegramProperties
{
    private String botToken;
    private List<String> chatIdList;
}
