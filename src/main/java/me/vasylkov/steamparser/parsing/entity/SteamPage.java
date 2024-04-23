package me.vasylkov.steamparser.parsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SteamPage implements Page
{
    private int currentPage;
    private int maxPage;
    private List<Listing> listings;
}
