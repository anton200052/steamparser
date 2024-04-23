package me.vasylkov.steamparser.data.entity;

public interface Item extends Validable
{
    String getHashName();
    Double getAveragePrice();
    String getListingsUrl();
    Double getMaximalItemMarkupPercentage();
    StickersModule getStickersModule();
}
