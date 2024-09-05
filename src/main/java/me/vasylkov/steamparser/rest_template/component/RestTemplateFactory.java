package me.vasylkov.steamparser.rest_template.component;

import me.vasylkov.steamparser.rest_template.entity.RestTemplateProxyWrapper;
import me.vasylkov.steamparser.rest_template.entity.RestTemplateWrapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateFactory
{
    public RestTemplate createDefaultRestTemplate()
    {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }

    public RestTemplateWrapper createRestTemplateWithProxy(RestTemplateProxyWrapper proxyWrapper)
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(proxyWrapper.getProxy().getRequestConfig());
        if (proxyWrapper.getProxy().getAuthHeaders() != null)
        {
            httpClientBuilder.setDefaultHeaders(proxyWrapper.getProxy().getAuthHeaders());
        }
        CloseableHttpClient httpClient = httpClientBuilder.build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(factory);

        return new RestTemplateWrapper(restTemplate, proxyWrapper);
    }
}
