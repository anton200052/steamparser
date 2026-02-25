package me.vasylkov.steamparser.price_api.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.vasylkov.steamparser.properties.PriceApiProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ItemPriceFetcher
{
    private final PriceApiProperties properties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public ItemPriceFetcher(PriceApiProperties properties, ObjectMapper objectMapper, RestTemplate restTemplate)
    {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    public double fetchItemAveragePrice(String priceApiUrl)
    {
        pauseBeforeRequest();
        double price = getAveragePriceFromApi(priceApiUrl);
        log.info("Item or Sticker price: {}", price);
        return price;
    }

    private void pauseBeforeRequest()
    {
        try
        {
            Thread.sleep(properties.getItemPriceFetcherDelay());
        }
        catch (InterruptedException e)
        {
            log.warn("Ошибка при ожидании перед получением цены предмета", e);
            Thread.currentThread().interrupt();
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
            log.error("Не удалось получить данные из API цен на предметы. Проверьте работоспособность сервиса!");
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
                log.error("Ответ от API не успешен!");
            }
        }
        catch (IOException e)
        {
            log.error("Ошибка при парсинге ответа от API!", e);
        }
        return 0.0;
    }
}
