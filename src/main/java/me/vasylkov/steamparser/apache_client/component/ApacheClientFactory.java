package me.vasylkov.steamparser.apache_client.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.apache_client.model.ApacheClientProxyWrapper;
import me.vasylkov.steamparser.apache_client.model.ApacheClientWrapper;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApacheClientFactory
{
    public ApacheClientWrapper createApacheClientWrapper(ApacheClientProxyWrapper proxyWrapper)
    {
        HttpHost httpHost = proxyWrapper.getProxy().getHttpHost();
        HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(httpHost);

        BasicCredentialsProvider credentialsProvider = proxyWrapper.getProxy().getBasicCredentialsProvider();

        CookieStore cookieStore = new BasicCookieStore();


        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .setDefaultCookieStore(cookieStore)
                .setConnectionManager(new PoolingHttpClientConnectionManager());

        if (credentialsProvider != null) {
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }

        CloseableHttpClient httpClient = httpClientBuilder.build();
        return new ApacheClientWrapper(httpClient, proxyWrapper);
    }

    public ApacheClientWrapper createApacheClientWrapper()
    {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        return new ApacheClientWrapper(httpClient, null);
    }
}
