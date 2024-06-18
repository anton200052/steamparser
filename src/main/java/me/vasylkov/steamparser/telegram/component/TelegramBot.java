package me.vasylkov.steamparser.telegram.component;

import me.vasylkov.steamparser.general.interfaces.MessagesSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer
{
    private final MessagesSender messagesSender;

    public TelegramBot(MessagesSender messagesSender)
    {
        this.messagesSender = messagesSender;
    }

    @Override
    public void consume(Update update)
    {
        if (update.hasMessage() && update.getMessage().hasText())
        {
            String text = update.getMessage().getText();
            if (text.equals("/ping"))
            {
                messagesSender.sendMessage("Pong!");
            }
        }
    }
}