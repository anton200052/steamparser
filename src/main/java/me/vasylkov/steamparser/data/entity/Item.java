package me.vasylkov.steamparser.data.entity;

public interface Item extends Validable
{
    String getHashName();
    void setHashName(String hashName);

    Double getAveragePrice();
    void setAveragePrice(Double averagePrice);

    Integer getMaximalPage();
    void setMaximalPage(Integer maximalPage);

    StickersModule getStickersModule();
    void setStickersModule(StickersModule stickersModule);

    Boolean getAvailable();
    void setAvailable(Boolean available);
}
