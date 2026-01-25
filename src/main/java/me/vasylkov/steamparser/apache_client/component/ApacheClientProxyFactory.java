package me.vasylkov.steamparser.apache_client.component;

import me.vasylkov.steamparser.apache_client.model.ApacheClientProxy;
import me.vasylkov.steamparser.apache_client.model.ApacheClientProxyWrapper;
import me.vasylkov.steamparser.common.abstraction.ProxyFactory;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.stereotype.Component;

@Component
public class ApacheClientProxyFactory implements ProxyFactory<ApacheClientProxyWrapper>
{
    @Override
    public ApacheClientProxyWrapper createDefaultProxy(String host, int port)
    {
        HttpHost httpHost = new HttpHost(host, port);
        return new ApacheClientProxyWrapper(new ApacheClientProxy(null, httpHost), false);
    }

    @Override
    public ApacheClientProxyWrapper createAuthProxy(String host, int port, String username, String password)
    {
        HttpHost httpHost = new HttpHost(host, port);
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(host, port),
                new UsernamePasswordCredentials(username, password.toCharArray()));

        return new ApacheClientProxyWrapper(new ApacheClientProxy(credentialsProvider, httpHost), false);
    }
}
