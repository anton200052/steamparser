package me.vasylkov.steamparser.parsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SteamSticker extends Sticker
{
    public SteamSticker(String hashName, double price)
    {
        super(hashName, price);
    }

    public SteamSticker()
    {
    }
}
