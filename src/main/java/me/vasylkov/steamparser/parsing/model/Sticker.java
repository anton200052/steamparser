package me.vasylkov.steamparser.parsing.model;

import lombok.Data;

@Data
public class Sticker
{
    private String hashName;
    private double price;

    public Sticker(String hashName) {
        this.hashName = hashName;
        this.price = 0.0;
    }

    public Sticker(String hashName, double price) {
        this.hashName = hashName;
        this.price = price;
    }
}
