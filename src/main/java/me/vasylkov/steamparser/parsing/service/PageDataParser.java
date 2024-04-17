package me.vasylkov.steamparser.parsing.service;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.service.ItemDataUrlGenerator;
import me.vasylkov.steamparser.httpclient.service.SteamItemPriceGetter;
import me.vasylkov.steamparser.parsing.entity.Listing;
import me.vasylkov.steamparser.parsing.entity.Page;
import me.vasylkov.steamparser.parsing.entity.Sticker;
import me.vasylkov.steamparser.parsing.entity.PageNumType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PageDataParser
{
    private final Logger logger;
    private final ItemDataUrlGenerator itemDataUrlGenerator;
    private final SteamItemPriceGetter steamItemPriceGetter;

    public Page parsePageDataToObject(WebDriver webDriver)
    {
        return new Page(parsePageNumber(webDriver, PageNumType.CURRENT), parsePageNumber(webDriver, PageNumType.MAX), parseListings(webDriver));
    }

    private int parsePageNumber(WebDriver webDriver, PageNumType pageNumType)
    {
        WebElement pageInfoElement = webDriver.findElement(By.className("info"));
        String pageInfoText = pageInfoElement.getText();

        String[] parts = pageInfoText.split(" from ");
        int pageNumber = 0;
        try
        {
            if (pageNumType == PageNumType.CURRENT)
                pageNumber = Integer.parseInt(parts[0]);
            else
                pageNumber = Integer.parseInt(parts[1]);
        }
        catch (NumberFormatException e)
        {
            logger.error("Не удалось преобразовать номер страницы в число: {}", e.getMessage());
        }
        return pageNumber;
    }

    private List<Listing> parseListings(WebDriver webDriver)
    {
        List<Listing> listings = new ArrayList<>();

        List<WebElement> listingsElements = webDriver.findElements(By.className("market_recent_listing_row"));
        for (WebElement listingElement : listingsElements)
        {
            if (!listingElement.findElements(By.cssSelector(".market_listing_price_without_fee")).isEmpty())
            {
                continue;
            }

            String listingHashName = listingElement.findElement(By.cssSelector(".market_listing_item_name")).getText();
            double listingPrice = Double.parseDouble(listingElement.findElement(By.cssSelector(".price_with")).getText().replaceAll("[^\\d,\\.]", "").replaceAll(",", "."));
            List<Sticker> listingStickers = new ArrayList<>();
            List<WebElement> stickersElements = listingElement.findElement(By.cssSelector("ul")).findElements(By.cssSelector("li"));

            for (WebElement stickerElement : stickersElements)
            {
                String stickerHashName = stickerElement.findElement(By.cssSelector(".sticker-image > img")).getAttribute("title");
                double stickerPrice = steamItemPriceGetter.getItemMedianPrice(itemDataUrlGenerator.generatePriceOverviewApiUrl(stickerHashName));
                listingStickers.add(new Sticker(stickerHashName, stickerPrice));
            }

            listings.add(new Listing(listingHashName, listingPrice, listingStickers, null, null));
        }

        return listings;
    }
}
