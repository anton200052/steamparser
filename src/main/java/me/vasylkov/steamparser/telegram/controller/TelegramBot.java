package me.vasylkov.steamparser.telegram.controller;

import lombok.RequiredArgsConstructor;

import me.vasylkov.steamparser.parsing.component.ItemsStorage;
import me.vasylkov.steamparser.parsing.service.ParsingTaskManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer
{
    private final ParsingTaskManager parsingTaskManager;
    private final ItemsStorage itemsStorage;

    @Override
    public void consume(Update update)
    {
        if (update.hasMessage() && update.getMessage().hasText())
        {
            String text = update.getMessage().getText();
            if (text.equals("/startparsing"))
            {
                parsingTaskManager.starParsingProcess();
            }
            else if (text.equals("/stopparsing"))
            {
                System.out.println(itemsStorage.getNext());
            }
        }
    }
}
