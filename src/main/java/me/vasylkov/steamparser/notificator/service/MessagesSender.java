package me.vasylkov.steamparser.notificator.service;

import me.vasylkov.steamparser.parsing.model.Sticker;

import java.util.List;

public interface MessagesSender
{
    void sendMessage(String message);
    void sendPhotoWithCaption(String caption, String photoUrl);
    void sendProfitableStickersItemData(String imgUrl, String hashName, double averageItemPrice, double currentPrice, int position, List<Sticker> stickers, double totalStickersPrice, double priceWithStickersMarkup, double stickersMarkupPercentage);
    void sendFloatItemData(String imgUrl, String hashName, double averageItemPrice, double currentPrice, int position, double itemFloat);
    void sendPatternItemData(String imgUrl, String hashName, double averageItemPrice, double currentPrice, int position, String pattern);
}
