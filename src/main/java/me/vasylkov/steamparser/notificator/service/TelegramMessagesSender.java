package me.vasylkov.steamparser.notificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.vasylkov.steamparser.parsing.model.Sticker;
import me.vasylkov.steamparser.properties.TelegramProperties;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramMessagesSender implements MessagesSender {
    private final TelegramClient client;
    private final TelegramProperties telegramProperties;

    @Override
    public void sendMessage(String text) {
        for (String chatId : telegramProperties.getChatIdList()) {
            SendMessage sendMessage = new SendMessage(chatId, text);
            try {
                client.execute(sendMessage);
            }
            catch (TelegramApiException e) {
                log.error("Произошла ошибка при отправке сообщения в тг. бота: ", e);
            }
        }
    }

    @Override
    public void sendPhotoWithCaption(String caption, String photoUrl) {
        for (String chatId : telegramProperties.getChatIdList()) {
            SendPhoto sendPhotoRequest = new SendPhoto(chatId, new InputFile(photoUrl));
            sendPhotoRequest.setCaption(caption);
            try {
                client.execute(sendPhotoRequest);
            }
            catch (TelegramApiException e) {
                log.error("Произошла ошибка при отправке фотографии в тг. бота: ", e);
            }
        }
    }

    private StringBuilder buildBaseCaption(String hashName, double averageItemPrice, double currentPrice, int position) {
        StringBuilder captionBuilder = new StringBuilder();
        captionBuilder.append("Название предмета: ").append(hashName).append("\n");
        captionBuilder.append("Средняя цена предмета: ").append(averageItemPrice).append("\n");
        captionBuilder.append("Текущая цена: ").append(currentPrice).append("\n");
        captionBuilder.append("Позиция / страница: ").append(position).append("\n");
        return captionBuilder;
    }

    /**
     * Sends information about a profitable item that contains stickers.
     */
    @Override
    public void sendProfitableStickersItemData(String imgUrl, String hashName, double averageItemPrice, double currentPrice, int position, List<Sticker> steamStickers, double totalStickersPrice, double priceWithStickersMarkup, double stickersMarkupPercentage) {
        StringBuilder captionBuilder = buildBaseCaption(hashName, averageItemPrice, currentPrice, position);

        captionBuilder.append("Стикеры:\n");
        for (Sticker steamSticker : steamStickers) {
            double roundedPrice = Math.round(steamSticker.getPrice() * 10.0) / 10.0; // округление до 1‑й десятичной
            captionBuilder.append(steamSticker.getHashName()).append(": (").append(roundedPrice).append(")\n");
        }

        double roundedTotalStickersPrice = Math.round(totalStickersPrice * 10.0) / 10.0;
        double roundedMarkupPrice = Math.round(priceWithStickersMarkup * 10.0) / 10.0;
        double roundedMarkupPercentage = Math.round(stickersMarkupPercentage * 10.0) / 10.0;

        captionBuilder.append("Общая стоимость стикеров: ").append(roundedTotalStickersPrice).append("\n");
        captionBuilder.append("\"Можно\" продать за: ").append(roundedMarkupPrice).append("\n");
        captionBuilder.append("Грязный \"профит\" в процентах: ").append(roundedMarkupPercentage).append("%\n");

        sendPhotoWithCaption(captionBuilder.toString(), imgUrl);
    }

    /**
     * Sends information about an item with a specific float value.
     *
     * @param itemFloat float value of the skin (0.0 – 1.0)
     */
    @Override
    public void sendFloatItemData(String imgUrl, String hashName, double averageItemPrice, double currentPrice, int position, BigDecimal itemFloat) {
        StringBuilder captionBuilder = buildBaseCaption(hashName, averageItemPrice, currentPrice, position);

        captionBuilder.append("Флот: ").append(itemFloat).append("\n");

        sendPhotoWithCaption(captionBuilder.toString(), imgUrl);
    }

    /**
     * Sends information about an item with a specific pattern.
     *
     * @param pattern human‑readable pattern code or id.
     */
    @Override
    public void sendPatternItemData(String imgUrl, String hashName, double averageItemPrice, double currentPrice, int position, String pattern) {
        StringBuilder captionBuilder = buildBaseCaption(hashName, averageItemPrice, currentPrice, position);

        captionBuilder.append("Паттерн: ").append(pattern).append("\n");

        sendPhotoWithCaption(captionBuilder.toString(), imgUrl);
    }
}
