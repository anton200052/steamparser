package me.vasylkov.steamparser.parsing.entity;

import java.util.List;

public interface Listing
{
    String getHashName();
    void setHashName(String hashName);

    double getPrice();
    void setPrice(double price);

    List<Sticker> getStickers();
    void setStickers(List<Sticker> steamStickers);

    String getImgUrl();
    void setImgUrl(String imgUrl);

    double getTotalStickersPrice();
    void setTotalStickersPrice(double totalStickersPrice);

    Double getPriceWithStickersMarkup();
    void setPriceWithStickersMarkup(Double priceWithStickersMarkup);

    Double getStickersMarkupPercentage();
    void setStickersMarkupPercentage(Double stickersMarkupPercentage);
}
