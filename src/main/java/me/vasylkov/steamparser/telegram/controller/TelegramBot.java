package me.vasylkov.steamparser.telegram.controller;

import lombok.RequiredArgsConstructor;

import me.vasylkov.steamparser.data.component.SteamItemsQueue;
import me.vasylkov.steamparser.parsing.service.SteamParsingTaskManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer
{
    private final SteamParsingTaskManager steamParsingTaskManager;

    @Override
    public void consume(Update update)
    {
        if (update.hasMessage() && update.getMessage().hasText())
        {
            String text = update.getMessage().getText();
            if (text.equals("/startparsing"))
            {
                steamParsingTaskManager.startParsingProcess();
            }
            else if (text.equals("/stopparsing"))
            {
                steamParsingTaskManager.stopParsingProcess();
            }
        }
    }
}
