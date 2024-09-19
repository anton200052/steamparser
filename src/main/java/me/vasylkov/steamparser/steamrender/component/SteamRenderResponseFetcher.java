package me.vasylkov.steamparser.steamrender.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.component.UrlGenerator;
import me.vasylkov.steamparser.steamrender.entity.SteamRenderResponse;
import me.vasylkov.steamparser.parsing.exception.UnknownErrorException;
import me.vasylkov.steamparser.parsing.exception.TooManyRequestsException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SteamRenderResponseFetcher
{
    private final UrlGenerator urlGenerator;

    public SteamRenderResponse fetchSteamRenderPageByItemName(CloseableHttpClient httpClient, String itemName, int pageNumber)
    {
        int start = pageNumber * 10;
        int count = 10;
        String url = urlGenerator.generateRenderUrl(itemName, start, count);

        try
        {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request))
            {
                int statusCode = response.getCode();

                if (statusCode == 429)
                {
                    throw new TooManyRequestsException("429 Too many requests");
                }

                String responseBody = EntityUtils.toString(response.getEntity());
                if (responseBody == null || responseBody.contains("\"success\":false"))
                {
                    throw new UnknownErrorException("Steam error, code: " + statusCode);
                }

                ObjectMapper objectMapper = new ObjectMapper();

                return objectMapper.readValue(responseBody, SteamRenderResponse.class);
            }
        }
        catch (IOException | ParseException e)
        {
            throw new RuntimeException("Error during Steam API request", e);
        }
    }
}
