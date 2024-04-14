package me.vasylkov.steamparser.httpclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.httpclient.entity.SteamItem;
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
public class ItemFetcher
{
    private final CloseableHttpClient httpClient;
    private final Logger logger;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SteamItem fetchItem(String apiUrl, String listingUrl, String name) //костыль
    {
        HttpGet httpGet = new HttpGet(apiUrl);
        SteamItem item = null;

        try (CloseableHttpResponse response = httpClient.execute(httpGet))
        {
            int statusCode = response.getCode();
            HttpEntity entity = response.getEntity();

            if (statusCode == HttpStatus.SC_OK && entity != null)
            {
                String responseContent = EntityUtils.toString(entity);
                item = objectMapper.readValue(responseContent, SteamItem.class);
            }
            else
            {
                logger.error("Не удалось обновить данные: {}", name);
            }
        }
        catch (IOException | ParseException e)
        {
            logger.error("Не удалось обновить данные: {}", name, e);
        }

        if (item != null)
        {
            item.setHashName(name);
            item.setUrl(listingUrl);
        }

        System.out.println(item);
        return item;
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
