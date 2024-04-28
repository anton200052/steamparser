package me.vasylkov.steamparser.telegram.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.general.interfaces.MessagesSender;
import me.vasylkov.steamparser.parsing.entity.Sticker;
import me.vasylkov.steamparser.telegram.configuration.TelegramProperties;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramMessagesSender implements MessagesSender
{
    private final TelegramClient client;
    private final Logger logger;
    private final TelegramProperties telegramProperties;

    @Override
    public void sendMessage(String text)
    {
        for (String chatId : telegramProperties.getChatIdList())
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

    @Override
    public void sendPhotoWithCaption(String caption, String photoUrl)
    {
        for (String chatId : telegramProperties.getChatIdList())
        {
            SendPhoto sendPhotoRequest = new SendPhoto(chatId, new InputFile(photoUrl));
            sendPhotoRequest.setCaption(caption);
            try
            {
                client.execute(sendPhotoRequest);
            }
            catch (TelegramApiException e)
            {
                logger.error("Произошла ошибка при отправке фотографии в тг. бота: ", e);
            }
        }
    }

    @Override
    public void sendProfitableItemData(String imgUrl, String hashName, double averageItemPrice, double currentPrice, int position, List<Sticker> stickers, double totalStickersPrice, double priceWithStickersMarkup, double stickersMarkupPercentage)
    {
        StringBuilder captionBuilder = new StringBuilder();

        captionBuilder.append("Название предмета: ").append(hashName).append("\n");
        captionBuilder.append("Средняя цена предмета: ").append(averageItemPrice).append("\n");
        captionBuilder.append("Текущая цена: ").append(currentPrice).append("\n");
        captionBuilder.append("Позиция / страница: ").append(position).append("\n");

        captionBuilder.append("Стикеры:\n");
        for (Sticker sticker : stickers) {
            double roundedPrice = Math.round(sticker.getPrice() * 10.0) / 10.0;  // Округление до 1 десятой
            captionBuilder.append(sticker.getHashName()).append(": (").append(roundedPrice).append(")\n");
        }

        double roundedTotalStickersPrice = Math.round(totalStickersPrice * 10.0) / 10.0;
        double roundedMarkupPrice = Math.round(priceWithStickersMarkup * 10.0) / 10.0;
        double roundedMarkupPercentage = Math.round(stickersMarkupPercentage * 10.0) / 10.0;

        captionBuilder.append("Общая стоимость стикеров: ").append(roundedTotalStickersPrice).append("\n");
        captionBuilder.append("\"Можно\" продать за: ").append(roundedMarkupPrice).append("\n");
        captionBuilder.append("Грязный \"профит\" в процентах: ").append(roundedMarkupPercentage).append("%\n");

        String caption = captionBuilder.toString();
        sendPhotoWithCaption(caption, imgUrl);
    }
}
