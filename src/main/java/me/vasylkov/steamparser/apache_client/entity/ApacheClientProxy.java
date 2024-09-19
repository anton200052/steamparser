package me.vasylkov.steamparser.apache_client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;

@Data
@AllArgsConstructor
public class ApacheClientProxy
{
    private BasicCredentialsProvider basicCredentialsProvider;
    private HttpHost httpHost;
}
