package me.vasylkov.steamparser.parsing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class Listing
{
    private String listingId;
    private String hashName;
    private double price;
    private List<Sticker> stickers;
    private BigDecimal floatValue;
    private int pattern;
    private String imgUrl;

    public Listing()
    {
    }
}