package me.vasylkov.steamparser.price_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.entity.Item;
import me.vasylkov.steamparser.price_api.configuration.PriceApiProperties;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
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
        logger.info("Average price is {}", price);
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
            return parseResponse(response);
        }
        catch (Exception e)
        {
            logger.error("Не удалось получить данные по URL: {}", priceApiUrl, e);
            return 0.0;
        }
    }

    private double parseResponse(ResponseEntity<String> response) throws IOException
    {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null)
        {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            if (jsonNode.get("success").asBoolean())
            {
                return jsonNode.get("price").get("average").asDouble();
            }
            else
            {
                logger.error("Апи отдал ответ success = false");
            }
        }
        return 0.0;
    }
}

