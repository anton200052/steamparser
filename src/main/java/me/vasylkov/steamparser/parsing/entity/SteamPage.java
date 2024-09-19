package me.vasylkov.steamparser.parsing.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SteamPage extends Page
{
    private int currentPage;
    private int maxPage;

    public SteamPage(List<Listing> listings, int currentPage, int maxPage)
    {
        super(listings);
        this.currentPage = currentPage;
        this.maxPage = maxPage;
    }

    public SteamPage(int currentPage, int maxPage)
    {
        this.currentPage = currentPage;
        this.maxPage = maxPage;
    }
}
