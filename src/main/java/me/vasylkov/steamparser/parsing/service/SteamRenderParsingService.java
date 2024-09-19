package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.apache_client.component.ApacheClientAndProxyChanger;
import me.vasylkov.steamparser.apache_client.component.ApacheClientFactory;
import me.vasylkov.steamparser.apache_client.entity.ApacheClientWrapper;
import me.vasylkov.steamparser.common.abstraction.MessagesSender;
import me.vasylkov.steamparser.data.component.ItemQueueManager;
import me.vasylkov.steamparser.data.component.UrlGenerator;
import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.parsing.component.PageAnalyser;
import me.vasylkov.steamparser.parsing.component.ParsingStatus;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.parsing.entity.AnalysingResult;
import me.vasylkov.steamparser.parsing.entity.ListingWithStickersMarkup;
import me.vasylkov.steamparser.parsing.entity.Page;
import me.vasylkov.steamparser.parsing.exception.TooManyRequestsException;
import me.vasylkov.steamparser.parsing.exception.UnknownErrorException;
import me.vasylkov.steamparser.steamrender.component.SteamPageFetcher;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SteamRenderParsingService implements ParsingService
{
    private final ApacheClientFactory apacheClientFactory;
    private final ParsingStatus parsingStatus;
    private final Logger logger;
    private final ItemQueueManager itemQueueManager;
    private final ParsingProperties parsingProperties;
    private final UrlGenerator urlGenerator;
    private final SteamPageFetcher steamPageFetcher;
    private final ApacheClientAndProxyChanger apacheClientAndProxyChanger;
    private final PageAnalyser pageAnalyser;
    private final MessagesSender messagesSender;

    public SteamRenderParsingService(ApacheClientFactory apacheClientFactory, ParsingStatus parsingStatus, Logger logger, ItemQueueManager itemQueueManager, ParsingProperties parsingProperties, UrlGenerator urlGenerator, SteamPageFetcher steamPageFetcher, ApacheClientAndProxyChanger apacheClientAndProxyChanger, PageAnalyser pageAnalyser, MessagesSender messagesSender)
    {
        this.apacheClientFactory = apacheClientFactory;
        this.parsingStatus = parsingStatus;
        this.logger = logger;
        this.itemQueueManager = itemQueueManager;
        this.parsingProperties = parsingProperties;
        this.urlGenerator = urlGenerator;
        this.steamPageFetcher = steamPageFetcher;
        this.apacheClientAndProxyChanger = apacheClientAndProxyChanger;
        this.pageAnalyser = pageAnalyser;
        this.messagesSender = messagesSender;
    }

    @Async
    @Override
    public void executeAsyncParsingTask()
    {
        parseItems();
    }


    private void parseItems()
    {
        ApacheClientWrapper apacheClientWrapper = null;
        try
        {
            apacheClientWrapper = apacheClientFactory.createApacheClientWrapper();
            Item lastAvailable = null;
            while (parsingStatus.isParsingStarted())
            {
                lastAvailable = processNextAvailableItem(apacheClientWrapper, lastAvailable);
                if (lastAvailable == null)
                {
                    return;
                }
            }
        }
        catch (Exception e)
        {
            logger.info("Произошла критическая ошибка парсинга. К сожалению поток будет остановлен", e);
        }
        finally
        {
            try
            {
                apacheClientWrapper.getCloseableHttpClient().close();
            }
            catch (IOException e)
            {
                logger.error("Ошибка закрытия клиента Apache");
            }
        }
    }

    private Item processNextAvailableItem(ApacheClientWrapper apacheClientWrapper, Item lastAvailable)
    {
        Item available = itemQueueManager.getAvailableOrLastItem(lastAvailable, parsingProperties.isCycle());
        if (available != null)
        {
            parseItem(available, apacheClientWrapper);

            if (parsingProperties.isCycle())
            {
                itemQueueManager.moveItemToLastAndUnblock(available);
            }
        }

        return available;
    }

    private void parseItem(Item item, ApacheClientWrapper apacheClientWrapper)
    {
        String itemName = item.getHashName();
        logger.info("Начинаем парсинг предмета {}", item.getHashName());
        int currentPageNum = 1;

        while (currentPageNum <= item.getMaximalPage())
        {
            if (!parsingStatus.isParsingStarted())
            {
                return;
            }

            Page steamPage = null;
            while (steamPage == null)
            {
                System.out.println(1);
                try
                {
                    steamPage = steamPageFetcher.fetchSteamPage(apacheClientWrapper.getCloseableHttpClient(), itemName, currentPageNum);
                }
                catch (TooManyRequestsException e)
                {
                    apacheClientAndProxyChanger.changeProxyAndApacheClient(apacheClientWrapper);
                    
                }
                catch (UnknownErrorException ignored)
                {

                }
            }

            AnalysingResult steamAnalysingResult = pageAnalyser.analysePage(steamPage, item);

            for (ListingWithStickersMarkup listing : steamAnalysingResult.getProfitableListings())
            {
                messagesSender.sendProfitableItemData(listing.getImgUrl(), listing.getHashName(), item.getAveragePrice(), listing.getPrice(), currentPageNum, listing.getStickers(), listing.getTotalStickersPrice(), listing.getPriceWithStickersMarkup(), listing.getStickersMarkupPercentage());
            }

            currentPageNum = currentPageNum + 1;
        }
    }
}
