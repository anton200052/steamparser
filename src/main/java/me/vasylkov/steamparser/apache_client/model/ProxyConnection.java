package me.vasylkov.steamparser.apache_client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProxyConnection extends ClientConnection {
    private BasicCredentialsProvider basicCredentialsProvider;
    private HttpHost httpHost;

    public ProxyConnection(BasicCredentialsProvider basicCredentialsProvider, HttpHost httpHost) {
        super();
        this.basicCredentialsProvider = basicCredentialsProvider;
        this.httpHost = httpHost;
    }
}
