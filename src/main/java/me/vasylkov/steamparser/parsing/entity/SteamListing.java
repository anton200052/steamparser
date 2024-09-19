package me.vasylkov.steamparser.parsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SteamListing extends Listing
{
    public SteamListing(String hashName, double price, List<Sticker> stickers, String imgUrl)
    {
        super(hashName, price, stickers, imgUrl);
    }

    public SteamListing()
    {
    }
}