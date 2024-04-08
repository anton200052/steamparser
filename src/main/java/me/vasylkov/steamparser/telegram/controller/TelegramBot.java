package me.vasylkov.steamparser.telegram.controller;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.longpolling.util.TelegramOkHttpClientFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer
{
    private final TelegramClient client;

    @Override
    public void consume(Update update)
    {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText())
        {
            // Create your send message object
            SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), update.getMessage().getText());
            try
            {
                client.execute(sendMessage);
            }
            catch (TelegramApiException e)
            {
                e.printStackTrace();
            }
        }
    }
}
