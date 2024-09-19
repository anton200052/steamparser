package me.vasylkov.steamparser.price_api.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.vasylkov.steamparser.price_api.configuration.PriceApiProperties;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class SteamItemPriceFetcher implements ItemPriceFetcher
{

    private final Logger logger;
    private final PriceApiProperties properties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public SteamItemPriceFetcher(Logger logger, PriceApiProperties properties, ObjectMapper objectMapper, RestTemplate restTemplate)
    {
        this.logger = logger;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public double fetchItemAveragePrice(String priceApiUrl)
    {
        pauseBeforeRequest();
        double price = getAveragePriceFromApi(priceApiUrl);
        logger.info("Price: {}", price);
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
        catch (RestClientException e)
        {
            logger.error("Не удалось получить данные из API цен на предметы. Проверьте работоспособность сервиса!");
        }

        return 0.0;
    }

    private double parseResponse(ResponseEntity<String> response)
    {
        try
        {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            if (jsonNode.get("success").asBoolean())
            {
                return jsonNode.get("priceAvg").asDouble();
            }
            else
            {
                logger.error("Ответ от API не успешен!");
            }
        }
        catch (IOException e)
        {
            logger.error("Ошибка при парсинге ответа от API!", e);
        }
        return 0.0;
    }
}
