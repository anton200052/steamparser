package me.vasylkov.steamparser.data.entity;

public interface Item extends Validable
{
    String getHashName();
    void setHashName(String hashName);

    Double getAveragePrice();
    void setAveragePrice(Double averagePrice);

    String getListingsUrl();
    void setListingsUrl(String listingsUrl);

    Double getMaximalItemMarkupPercentage();
    void setMaximalItemMarkupPercentage(Double maximalItemMarkupPercentage);

    StickersModule getStickersModule();
    void setStickersModule(StickersModule stickersModule);
}
