package me.vasylkov.steamparser.apache_client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

@Data
@AllArgsConstructor
public class ApacheClientWrapper
{
    private CloseableHttpClient closeableHttpClient;
    private ApacheClientProxy apacheClientProxy;
}
