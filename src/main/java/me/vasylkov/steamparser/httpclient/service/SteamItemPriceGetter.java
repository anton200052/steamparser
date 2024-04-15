package me.vasylkov.steamparser.httpclient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SteamItemPriceGetter
{
    private final CloseableHttpClient httpClient;
    private final Logger logger;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public double getItemMedianPrice(String priceOverviewApiUrl)
    {
        HttpGet httpGet = new HttpGet(priceOverviewApiUrl);
        double medianPrice = 0.0;

        try (CloseableHttpResponse response = httpClient.execute(httpGet))
        {
            int statusCode = response.getCode();
            HttpEntity entity = response.getEntity();

            if (statusCode == HttpStatus.SC_OK && entity != null)
            {
                String responseContent = EntityUtils.toString(entity);
                JsonNode jsonNode = objectMapper.readTree(responseContent);
                String medianPriceStr = jsonNode.get("median_price").asText();

                // Remove currency symbols and replace comma with a period
                medianPriceStr = medianPriceStr.replaceAll("[^\\d,\\.]", "").replace(',', '.');
                medianPrice = Double.parseDouble(medianPriceStr);
            }
            else
            {
                logger.error("Failed to fetch data: {}", priceOverviewApiUrl);
            }
        }
        catch (IOException | ParseException e)
        {
            logger.error("Failed to fetch data: {}", priceOverviewApiUrl, e);
        }

        System.out.println("Median Price: " + medianPrice);
        return medianPrice;
    }


    @PreDestroy
    public void destroy()
    {
        try
        {
            httpClient.close();
        }
        catch (IOException e)
        {
            logger.error("Error closing HttpClient", e);
        }
    }
}
