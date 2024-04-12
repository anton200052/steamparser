package me.vasylkov.steamparser.httpclient.configuration;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class HttpClientConfiguration
{

    @Bean
    public CloseableHttpClient httpClient()
    {
        return HttpClients.createDefault();
    }
}
