package me.vasylkov.steamparser.apache_client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

@Data
@AllArgsConstructor
public class ApacheClientWrapper
{
    private CloseableHttpClient closeableHttpClient;
    private ApacheClientProxyWrapper apacheClientProxyWrapper;
}
