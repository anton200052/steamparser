package me.vasylkov.steamparser.apache_client.component;

import me.vasylkov.steamparser.apache_client.model.ClientConnection;
import me.vasylkov.steamparser.apache_client.model.ProxyConnection;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.stereotype.Component;

@Component
public class ProxyConnectionFactory
{
    public ClientConnection createDefaultProxy(String host, int port)
    {
        HttpHost httpHost = new HttpHost(host, port);
        return new ProxyConnection(null, httpHost);
    }

    public ClientConnection createAuthProxy(String host, int port, String username, String password)
    {
        HttpHost httpHost = new HttpHost(host, port);
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(host, port),
                new UsernamePasswordCredentials(username, password.toCharArray()));

        return new ProxyConnection(credentialsProvider, httpHost);
    }
}
