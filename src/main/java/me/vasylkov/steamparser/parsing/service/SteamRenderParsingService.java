package me.vasylkov.steamparser.parsing.service;

import me.vasylkov.steamparser.apache_client.component.ApacheClientAndProxyChanger;
import me.vasylkov.steamparser.apache_client.component.ApacheClientFactory;
import me.vasylkov.steamparser.apache_client.component.ApacheClientProxyManager;
import me.vasylkov.steamparser.apache_client.model.ApacheClientProxy;
import me.vasylkov.steamparser.apache_client.model.ApacheClientWrapper;
import me.vasylkov.steamparser.notificator.service.MessagesSender;
import me.vasylkov.steamparser.config_data.component.ItemQueueManager;
import me.vasylkov.steamparser.config_data.component.UrlGenerator;
import me.vasylkov.steamparser.config_data.model.PricedItem;
import me.vasylkov.steamparser.parsing.component.BlacklistChecker;
import me.vasylkov.steamparser.parsing.component.PageAnalyser;
import me.vasylkov.steamparser.parsing.component.ParsingStatus;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.parsing.model.AnalysingResult;
import me.vasylkov.steamparser.parsing.model.ListingWithStickersMarkup;
import me.vasylkov.steamparser.parsing.model.ProfitableListing;
import me.vasylkov.steamparser.parsing.model.Page;
import me.vasylkov.steamparser.parsing.exception.TooManyRequestsException;
import me.vasylkov.steamparser.parsing.exception.UnknownErrorException;
import me.vasylkov.steamparser.parsing.component.SteamPageFetcher;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SteamRenderParsingService implements ParsingService {
    private final ApacheClientFactory apacheClientFactory;
    private final ParsingStatus parsingStatus;
    private final Logger logger;
    private final ItemQueueManager itemQueueManager;
    private final ParsingProperties parsingProperties;
    private final SteamPageFetcher steamPageFetcher;
    private final PageAnalyser pageAnalyser;
    private final MessagesSender messagesSender;
    private final BlacklistChecker blacklistChecker;
    private final ApacheClientProxyManager apacheClientProxyManager;

    public SteamRenderParsingService(ApacheClientFactory apacheClientFactory, ParsingStatus parsingStatus, Logger logger, ItemQueueManager itemQueueManager, ParsingProperties parsingProperties, SteamPageFetcher steamPageFetcher, PageAnalyser pageAnalyser, MessagesSender messagesSender, BlacklistChecker blacklistChecker, ApacheClientProxyManager apacheClientProxyManager) {
        this.apacheClientFactory = apacheClientFactory;
        this.parsingStatus = parsingStatus;
        this.logger = logger;
        this.itemQueueManager = itemQueueManager;
        this.parsingProperties = parsingProperties;
        this.steamPageFetcher = steamPageFetcher;
        this.pageAnalyser = pageAnalyser;
        this.messagesSender = messagesSender;
        this.blacklistChecker = blacklistChecker;
        this.apacheClientProxyManager = apacheClientProxyManager;
    }

    @Async
    @Override
    public void executeParsingTask() {
        parseItems();
    }

    private void parseItems() {
        try {
            while (parsingStatus.isParsingStarted()) {
                PricedItem available = processNextAvailableItem();
                if (available == null) {
                    return;
                }
            }
        } catch (Exception e) {
            logger.info("Произошла критическая ошибка парсинга. К сожалению поток будет остановлен", e);
        } finally {
            try {
                apacheClientWrapper.getCloseableHttpClient().close();
            } catch (IOException | NullPointerException e) {
                logger.error("Ошибка закрытия клиента Apache");
            }
        }
    }

    private PricedItem processNextAvailableItem() {
        PricedItem available = itemQueueManager.getAvailableItem();
        if (available != null) {
            parseItem(available);

            if (parsingProperties.isCycle()) {
                itemQueueManager.moveItemToLastAndUnblock(available);
            }
        }

        return available;
    }

    private void parseItem(PricedItem item) throws InterruptedException {
        String itemName = item.getHashName();
        int currentPageNum = 1;
        int maximalAllowedPage = 1;

        while (currentPageNum <= item.getMaximalPage() && currentPageNum <= maximalAllowedPage) {
            if (!parsingStatus.isParsingStarted()) {
                return;
            }
            logger.info("Parsing item: {}, Page: {}", item.getHashName(), currentPageNum);

            Page page = fetchItemPage(itemName, currentPageNum);
            if (page == null) {
                continue;
            }

            maximalAllowedPage = page.getMaxPage();
            currentPageNum = currentPageNum + 1;

            AnalysingResult steamAnalysingResult = pageAnalyser.analysePage(steamPage, item);

            for (ProfitableListing listing : steamAnalysingResult.getProfitableListings()) {
                switch (listing.getProfitableListingType()) {
                    case STICKERS -> {
                        if (listing instanceof ListingWithStickersMarkup stickersListing) {
                            messagesSender.sendProfitableStickersItemData(stickersListing.getImgUrl(), stickersListing.getHashName(), item.getAveragePrice(), stickersListing.getPrice(), currentPageNum, stickersListing.getStickers(), stickersListing.getTotalStickersPrice(), stickersListing.getPriceWithStickersMarkup(), stickersListing.getStickersMarkupPercentage());
                        }
                    }
                    case FLOAT ->
                            messagesSender.sendFloatItemData(listing.getImgUrl(), listing.getHashName(), item.getAveragePrice(), listing.getPrice(), currentPageNum, listing.getFloatValue());
                    case PATTERN ->
                            messagesSender.sendPatternItemData(listing.getImgUrl(), listing.getHashName(), item.getAveragePrice(), listing.getPrice(), currentPageNum, String.valueOf(listing.getPattern()));
                }
                blacklistChecker.addToBlacklist(listing.getListingId());
            }
        }
    }

    private Page fetchItemPage(String itemName, int pageNum) throws InterruptedException {
        Page page = null;
        ApacheClientProxy proxy = getAvailableProxy();
        try(CloseableHttpClient client = getCloseableHttpClient(proxy)) {
            page = steamPageFetcher.fetchSteamPage(client, itemName, pageNum);
        } catch (TooManyRequestsException e) {
            logger.info("Too many requests");
        } catch (UnknownErrorException ignored) {
            logger.info("Unknown error, retry");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return page;
    }

    private ApacheClientProxy getAvailableProxy() throws InterruptedException {
        return apacheClientProxyManager.borrowProxy();
    }

    private CloseableHttpClient getCloseableHttpClient(ApacheClientProxy apacheClientProxy) {
        if (apacheClientProxy != null) {
            return apacheClientFactory.createApacheClient(apacheClientProxy);
        } else {
            return apacheClientFactory.createApacheClient();
        }
    }
}
