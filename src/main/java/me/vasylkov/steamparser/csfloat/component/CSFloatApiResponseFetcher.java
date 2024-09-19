package me.vasylkov.steamparser.csfloat.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.csfloat.entity.CSFloatApiResponse;
import me.vasylkov.steamparser.data.component.UrlGenerator;
import me.vasylkov.steamparser.parsing.exception.UnknownErrorException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CSFloatApiResponseFetcher
{
    private final UrlGenerator urlGenerator;
    private final RestTemplate restTemplate;

    public CSFloatApiResponse fetchCSFloatResponse(String url)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", "https://csfloat.com");
        headers.set("Accept", "*/*");
        headers.set("Accept-Encoding", "keep-alive");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<CSFloatApiResponse> csFloatApiResponseRequestEntity = restTemplate.exchange(urlGenerator.generateCSFloatUrl(url), HttpMethod.GET, entity, CSFloatApiResponse.class);
        CSFloatApiResponse csFloatApiResponse = csFloatApiResponseRequestEntity.getBody();
        int statusCode = csFloatApiResponseRequestEntity.getStatusCode().value();

        if (statusCode != 200)
        {
            throw new UnknownErrorException("CSFloat error, code: " + statusCode);
        }

        return csFloatApiResponse;
    }
}
