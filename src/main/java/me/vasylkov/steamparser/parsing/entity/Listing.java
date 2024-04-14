package me.vasylkov.steamparser.parsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class Listing
{
    private String hashName;
    private double price;
    private List<Sticker> stickers;
}
