package me.vasylkov.steamparser.parsing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
public class Page
{
    @Override
    public String toString() {
        return "Page{" + "listings=" + listings + ", currentPage=" + currentPage + ", maxPage=" + maxPage + '}';
    }

    private List<Listing> listings;
    private int currentPage;
    private int maxPage;
}
