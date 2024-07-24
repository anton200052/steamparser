package me.vasylkov.steamparser.rest_template.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;

import java.util.List;

@Data
@AllArgsConstructor
public class RestTemplateProxy
{
    private RequestConfig requestConfig;
    private List<Header> authHeaders;
}
