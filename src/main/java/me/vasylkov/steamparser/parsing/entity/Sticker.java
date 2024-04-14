package me.vasylkov.steamparser.parsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class Sticker
{
    private String hashName;
    private double price;
}
