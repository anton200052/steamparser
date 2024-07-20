package me.vasylkov.steamparser.data.entity;

public interface Item extends Validable
{
    String getHashName();
    void setHashName(String hashName);

    Double getAveragePrice();
    void setAveragePrice(Double averagePrice);

    String getListingsUrl();
    void setListingsUrl(String listingsUrl);

    Integer getMaximalPage();
    void setMaximalPage(Integer maximalPage);

    StickersModule getStickersModule();
    void setStickersModule(StickersModule stickersModule);

    Boolean getAvailable();
    void setAvailable(Boolean available);
}
