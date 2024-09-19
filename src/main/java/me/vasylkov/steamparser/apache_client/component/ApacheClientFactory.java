package me.vasylkov.steamparser.apache_client.component;

import me.vasylkov.steamparser.apache_client.entity.ApacheClientProxy;
import me.vasylkov.steamparser.apache_client.entity.ApacheClientProxyWrapper;
import me.vasylkov.steamparser.apache_client.entity.ApacheClientWrapper;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.impl.routing.DefaultRoutePlanner;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.stereotype.Component;

@Component
public class ApacheClientFactory
{
    public ApacheClientWrapper createApacheClientWrapper(ApacheClientProxyWrapper proxyWrapper)
    {
        HttpHost httpHost = proxyWrapper.getProxy().getHttpHost();
        HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(httpHost);

        BasicCredentialsProvider credentialsProvider = proxyWrapper.getProxy().getBasicCredentialsProvider();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        if (credentialsProvider != null)
        {
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }

        CloseableHttpClient httpClient = httpClientBuilder.setRoutePlanner(routePlanner)
            .setConnectionManager(new PoolingHttpClientConnectionManager())
            .build();

        return new ApacheClientWrapper(httpClient, proxyWrapper);
    }

    public ApacheClientWrapper createApacheClientWrapper()
    {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        return new ApacheClientWrapper(httpClient, null);
    }
}
