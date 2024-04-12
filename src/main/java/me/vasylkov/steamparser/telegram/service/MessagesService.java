package me.vasylkov.steamparser.telegram.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@RequiredArgsConstructor
public class MessagesService
{
    private final TelegramClient client;
    private final Logger logger;

    public void sendMessage(String chatId, String text)
    {
        SendMessage sendMessage = new SendMessage(chatId, text);
        try
        {
            client.execute(sendMessage);
        }
        catch (TelegramApiException e)
        {
            logger.error("Произошла ошибка при отправке сообщения в тг. бота: ", e);
        }
    }
}
