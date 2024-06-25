package me.vasylkov.steamparser.telegram.configuration;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.general.component.TempFilesCleaner;
import me.vasylkov.steamparser.telegram.component.TelegramBot;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class TelegramBotConfiguration
{
    private final TelegramBot bot;
    private final TelegramProperties properties;
    private final Logger logger;

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsLongPollingApplication() throws TelegramApiException
    {
        TelegramBotsLongPollingApplication telegramBotsLongPollingApplication = new TelegramBotsLongPollingApplication();
        telegramBotsLongPollingApplication.registerBot(properties.getBotToken(), bot);
        logger.info("Телеграм бот загружен");
        return telegramBotsLongPollingApplication;
    }
}
