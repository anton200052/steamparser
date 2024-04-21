package me.vasylkov.steamparser.general.interfaces;

import me.vasylkov.steamparser.parsing.entity.Sticker;

import java.util.List;

public interface MessagesSender
{
    void sendMessage(String message);
    void sendPhotoWithCaption(String caption, String photoUrl);
    void sendProfitableItemData(String imgUrl, String hashName, int position, List<Sticker> stickers, double totalStickersPrice, Double priceWithStickersMarkup, Double stickersMarkupPercentage);
}
