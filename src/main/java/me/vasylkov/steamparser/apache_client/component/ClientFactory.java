package me.vasylkov.steamparser.apache_client.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.apache_client.model.ClientConnection;
import me.vasylkov.steamparser.apache_client.model.ProxyConnection;
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
public class ClientFactory
{
    public CloseableHttpClient createApacheClient(ClientConnection connection)
    {
        if (connection instanceof ProxyConnection proxy) {
            HttpHost httpHost = proxy.getHttpHost();
            HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(httpHost);

            BasicCredentialsProvider credentialsProvider = proxy.getBasicCredentialsProvider();

            CookieStore cookieStore = new BasicCookieStore();


            HttpClientBuilder httpClientBuilder = HttpClients.custom()
                    .setRoutePlanner(routePlanner)
                    .setDefaultCookieStore(cookieStore)
                    .setConnectionManager(new PoolingHttpClientConnectionManager());

            if (credentialsProvider != null) {
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }

            return httpClientBuilder.build();
        }
        else {
            return HttpClients.createDefault();
        }
    }
}
