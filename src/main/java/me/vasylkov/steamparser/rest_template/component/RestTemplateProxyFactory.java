package me.vasylkov.steamparser.rest_template.component;

import me.vasylkov.steamparser.common.abstraction.ProxyFactory;
import me.vasylkov.steamparser.rest_template.entity.RestTemplateProxy;
import me.vasylkov.steamparser.rest_template.entity.RestTemplateProxyWrapper;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.message.BasicHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class RestTemplateProxyFactory implements ProxyFactory<RestTemplateProxyWrapper>
{
    @Override
    public RestTemplateProxyWrapper createDefaultProxy(String host, int port)
    {
        HttpHost httpHost = new HttpHost(host, port);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();

        return new RestTemplateProxyWrapper(new RestTemplateProxy(requestConfig, null), false);
    }

    @Override
    public RestTemplateProxyWrapper createAuthProxy(String host, int port, String username, String password)
    {
        String credentials = host + ":" + port;
        String encodedAuthorization = Base64.getEncoder().encodeToString(credentials.getBytes());

        Header header = new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuthorization);
        List<Header> headers = new ArrayList<>();
        headers.add(header);

        HttpHost httpHost = new HttpHost(host, port);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();

        return new RestTemplateProxyWrapper(new RestTemplateProxy(requestConfig, headers), false);
    }
}
