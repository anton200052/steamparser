package me.vasylkov.steamparser.parsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SteamSticker implements Sticker
{
    private String hashName;
    private double price;
}
