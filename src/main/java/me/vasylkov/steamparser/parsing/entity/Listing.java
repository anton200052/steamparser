package me.vasylkov.steamparser.parsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Listing
{
    protected String hashName;
    protected double price;
    protected List<Sticker> stickers;
    protected String imgUrl;
}
