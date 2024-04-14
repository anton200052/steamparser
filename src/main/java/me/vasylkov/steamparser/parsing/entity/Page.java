package me.vasylkov.steamparser.parsing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class Page
{
    private int currentPage;
    private int maxPage;
    private List<Listing> listings;
}
