package me.vasylkov.steamparser.selenium.component;

import me.vasylkov.steamparser.data.component.SteamItemUrlGenerator;
import me.vasylkov.steamparser.parsing.entity.Listing;
import me.vasylkov.steamparser.parsing.entity.Page;
import me.vasylkov.steamparser.parsing.entity.Sticker;
import me.vasylkov.steamparser.parsing.entity.SteamListing;
import me.vasylkov.steamparser.parsing.entity.SteamPage;
import me.vasylkov.steamparser.parsing.entity.SteamSticker;
import me.vasylkov.steamparser.parsing.enums.PageNumType;
import me.vasylkov.steamparser.price_api.component.ItemPriceFetcher;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class SteamSeleniumPageDataParser implements SeleniumPageDataParser
{
    private final Logger logger;
    private final SteamItemUrlGenerator steamItemUrlGenerator;

    @Qualifier("steamItemPriceFetcher")
    private final ItemPriceFetcher itemPriceFetcher;

    public SteamSeleniumPageDataParser(Logger logger, SteamItemUrlGenerator steamItemUrlGenerator, ItemPriceFetcher itemPriceFetcher)
    {
        this.logger = logger;
        this.steamItemUrlGenerator = steamItemUrlGenerator;
        this.itemPriceFetcher = itemPriceFetcher;
    }

    @Override
    public Page parsePageDataToObject(WebDriver webDriver)
    {
        return new SteamPage(parseListings(webDriver), parsePageNumber(webDriver, PageNumType.CURRENT), parsePageNumber(webDriver, PageNumType.MAX));
    }

    private int parsePageNumber(WebDriver webDriver, PageNumType pageNumType)
    {
        int pageNumber = -1;
        try
        {
            WebElement pageInfoElement = webDriver.findElement(By.className("info"));
            String pageInfoText = pageInfoElement.getText();

            String[] parts = pageInfoText.split(" from ");
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
        }
        catch (NoSuchElementException | StaleElementReferenceException e)
        {
            logger.error("Ошибка при парсинге номера страницы, будет возвращено число <-1>", e);
        }

        return pageNumber;
    }

    private List<Listing> parseListings(WebDriver webDriver)
    {
        List<Listing> listings = new ArrayList<>();

        try
        {
            List<WebElement> listingsElements = webDriver.findElements(By.className("market_recent_listing_row"));
            for (WebElement listingElement : listingsElements)
            {
                if (!listingElement.findElements(By.cssSelector(".market_listing_price_without_fee")).isEmpty())
                {
                    continue;
                }

                String listingHashName = listingElement.findElement(By.cssSelector(".market_listing_item_name")).getText();
                String imgUrl = listingElement.findElement(By.cssSelector(".market_listing_item_img_container > img")).getAttribute("src");
                double listingPrice = Double.parseDouble(listingElement.findElement(By.cssSelector(".price_with")).getText().replaceAll("[^\\d,\\.]", "").replaceAll(",", "."));
                List<Sticker> listingSteamStickers = new ArrayList<>();
                List<WebElement> stickersElements = listingElement.findElement(By.cssSelector("ul")).findElements(By.cssSelector("li"));

                for (WebElement stickerElement : stickersElements)
                {
                    String stickerHashName = stickerElement.findElement(By.cssSelector(".sticker-image > img")).getAttribute("title");
                    double stickerPrice = itemPriceFetcher.fetchItemAveragePrice(steamItemUrlGenerator.generatePriceOverviewApiUrl(stickerHashName));
                    listingSteamStickers.add(new SteamSticker(stickerHashName, stickerPrice));
                }

                listings.add(new SteamListing(listingHashName, listingPrice, listingSteamStickers, imgUrl));
            }
        }
        catch (NoSuchElementException | StaleElementReferenceException e)
        {
            logger.error("Ошибка при парсинге листингов, будет возвращен пустой список", e);
        }
        return listings;
    }
}
