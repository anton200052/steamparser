package me.vasylkov.steamparser.parsing.service;

import lombok.extern.slf4j.Slf4j;
import me.vasylkov.steamparser.apache_client.component.ClientFactory;
import me.vasylkov.steamparser.apache_client.component.ClientConnectionManager;
import me.vasylkov.steamparser.apache_client.model.ClientConnection;
import me.vasylkov.steamparser.notificator.service.MessagesSender;
import me.vasylkov.steamparser.config_data.component.ItemQueueManager;
import me.vasylkov.steamparser.config_data.model.PricedItem;
import me.vasylkov.steamparser.parsing.component.BlacklistChecker;
import me.vasylkov.steamparser.parsing.component.PageAnalyser;
import me.vasylkov.steamparser.parsing.component.ParsingStatus;
import me.vasylkov.steamparser.properties.ParsingProperties;
import me.vasylkov.steamparser.parsing.model.AnalysingResult;
import me.vasylkov.steamparser.parsing.model.ListingWithStickersMarkup;
import me.vasylkov.steamparser.parsing.model.ProfitableListing;
import me.vasylkov.steamparser.parsing.model.Page;
import me.vasylkov.steamparser.parsing.exception.TooManyRequestsException;
import me.vasylkov.steamparser.parsing.exception.UnknownErrorException;
import me.vasylkov.steamparser.parsing.exception.NoAvailableConnectionException;
import me.vasylkov.steamparser.parsing.component.SteamPageFetcher;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class SteamRenderParsingService implements ParsingService {
    private final ClientFactory clientFactory;
    private final ParsingStatus parsingStatus;
    private final ItemQueueManager itemQueueManager;
    private final ParsingProperties parsingProperties;
    private final SteamPageFetcher steamPageFetcher;
    private final PageAnalyser pageAnalyser;
    private final MessagesSender messagesSender;
    private final BlacklistChecker blacklistChecker;
    private final ClientConnectionManager clientConnectionManager;

    public SteamRenderParsingService(ClientFactory clientFactory, ParsingStatus parsingStatus, ItemQueueManager itemQueueManager, ParsingProperties parsingProperties, SteamPageFetcher steamPageFetcher, PageAnalyser pageAnalyser, MessagesSender messagesSender, BlacklistChecker blacklistChecker, ClientConnectionManager clientConnectionManager) {
        this.clientFactory = clientFactory;
        this.parsingStatus = parsingStatus;
        this.itemQueueManager = itemQueueManager;
        this.parsingProperties = parsingProperties;
        this.steamPageFetcher = steamPageFetcher;
        this.pageAnalyser = pageAnalyser;
        this.messagesSender = messagesSender;
        this.blacklistChecker = blacklistChecker;
        this.clientConnectionManager = clientConnectionManager;
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
            log.info("Произошла критическая ошибка парсинга. К сожалению поток будет остановлен", e);
        }
    }

    private PricedItem processNextAvailableItem() throws InterruptedException {
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
            log.info("Parsing item: {}, Page: {}", item.getHashName(), currentPageNum);

            Page page = fetchItemPage(itemName, currentPageNum);
            if (page == null) {
                continue;
            }

            maximalAllowedPage = page.getMaxPage();

            analysePageAndSendParsingResults(currentPageNum, page, item);

            currentPageNum = currentPageNum + 1;
        }
    }

    private Page fetchItemPage(String itemName, int pageNum) throws InterruptedException {
        Page page = null;
        int waitingTimeMillis = parsingProperties.getConnectionRequestsDelay();
        ClientConnection connection = getAvailableConnection();

        try(CloseableHttpClient client = getCloseableHttpClient(connection)) {
            page = steamPageFetcher.fetchSteamPage(client, itemName, pageNum);
        } catch (TooManyRequestsException e) {
            log.warn("Too many requests");
            waitingTimeMillis = parsingProperties.getConnectionBlockingTime();
        } catch (UnknownErrorException ignored) {
            log.warn("Unknown error, retry");
        } catch (IOException e) {
            log.warn("Error while closing http client");
        } finally {
            clientConnectionManager.returnConnection(connection, waitingTimeMillis);
        }

        return page;
    }

    private ClientConnection getAvailableConnection() throws InterruptedException {
        ClientConnection connection = clientConnectionManager.borrowConnection();
        if (connection == null) {
            throw new NoAvailableConnectionException("Connection queue returned null");
        }
        return connection;
    }

    private CloseableHttpClient getCloseableHttpClient(ClientConnection connection) throws InterruptedException {
        return clientFactory.createApacheClient(connection);
    }

    private void analysePageAndSendParsingResults(int pageNum, Page page, PricedItem item) {
        AnalysingResult steamAnalysingResult = pageAnalyser.analysePage(page, item);

        for (ProfitableListing listing : steamAnalysingResult.getProfitableListings()) {
            switch (listing.getProfitableListingType()) {
                case STICKERS -> {
                    if (listing instanceof ListingWithStickersMarkup stickersListing) {
                        messagesSender.sendProfitableStickersItemData(stickersListing.getImgUrl(), stickersListing.getHashName(), item.getAveragePrice(), stickersListing.getPrice(), pageNum, stickersListing.getStickers(), stickersListing.getTotalStickersPrice(), stickersListing.getPriceWithStickersMarkup(), stickersListing.getStickersMarkupPercentage());
                    }
                }
                case FLOAT ->
                        messagesSender.sendFloatItemData(listing.getImgUrl(), listing.getHashName(), item.getAveragePrice(), listing.getPrice(), pageNum, listing.getFloatValue());
                case PATTERN ->
                        messagesSender.sendPatternItemData(listing.getImgUrl(), listing.getHashName(), item.getAveragePrice(), listing.getPrice(), pageNum, String.valueOf(listing.getPattern()));
            }
            blacklistChecker.addToBlacklist(listing.getListingId());
        }
    }
}
