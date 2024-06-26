package me.vasylkov.steamparser.parsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamListing implements Listing
{
    private String hashName;
    private double price;
    private List<Sticker> stickers;
    private String imgUrl;
    private double totalStickersPrice;
    private Double priceWithStickersMarkup;
    private Double stickersMarkupPercentage;
}