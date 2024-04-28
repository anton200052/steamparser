package me.vasylkov.steamparser.price_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.price_api.configuration.PriceApiProperties;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class SteamItemPriceFetcher implements ItemPriceFetcher
{

    private final Logger logger;
    private final RestTemplate restTemplate;
    private final PriceApiProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public double fetchItemAveragePrice(String priceApiUrl)
    {
        pauseBeforeRequest();
        double price = getAveragePriceFromApi(priceApiUrl);
        logger.info("Цена предмета/стикера {}", price);
        return price;
    }

    private void pauseBeforeRequest()
    {
        try
        {
            Thread.sleep(properties.getItemPriceFetcherDuration() * 1000L);
        }
        catch (InterruptedException e)
        {
            logger.warn("Ошибка при ожидании перед получением цены предмета", e);
            Thread.currentThread().interrupt(); // Восстановление прерванного статуса потока
        }
    }

    private double getAveragePriceFromApi(String priceApiUrl)
    {
        try
        {
            ResponseEntity<String> response = restTemplate.getForEntity(priceApiUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null)
            {
                return parseResponse(response);
            }
        }
        catch (HttpClientErrorException e)
        {
            logger.error("Не удалось получить данные по URL: {}. С причиной {}", priceApiUrl, e.getMessage());
        }

        return 0.0;
    }

    private double parseResponse(ResponseEntity<String> response)
    {
        try
        {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("price").get("average").asDouble();
        }
        catch (IOException e)
        {
            logger.error("Ошибка при парсинге ответа от API!");
        }
        return 0.0;
    }
}

