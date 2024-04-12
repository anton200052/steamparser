package me.vasylkov.steamparser.telegram.configuration;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@RequiredArgsConstructor
public class TelegramClientConfiguration
{
    private final TelegramProperties properties;

    @Bean
    public TelegramClient telegramClient()
    {
        return new OkHttpTelegramClient(properties.getBotToken());
    }
}
