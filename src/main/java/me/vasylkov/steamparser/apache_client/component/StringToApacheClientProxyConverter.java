package me.vasylkov.steamparser.apache_client.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.apache_client.model.ApacheClientProxy;
import me.vasylkov.steamparser.apache_client.model.ProxyType;
import org.slf4j.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StringToApacheClientProxyConverter implements Converter<String, ApacheClientProxy> {
    private final ProxyValidator proxyValidator;
    private final ApacheClientProxyFactory proxyFactory;
    private final Logger logger;

    @Override
    public ApacheClientProxy convert(String source) {

        ProxyType proxyType = proxyValidator.getProxyType(source);
        if (proxyType == ProxyType.INVALID) {
            logger.error("Proxy {} in wrong format, this proxy will not be used.", source);
            return null;
        }

        String[] parts = source.split("@");
        String[] ipAndPort = parts[0].split(":");

        String address = ipAndPort[0];
        int port = Integer.parseInt(ipAndPort[1]);

        ApacheClientProxy proxy;
        if (proxyType == ProxyType.DEFAULT) {
            proxy = proxyFactory.createDefaultProxy(address, port);
        } else {
            String[] usernameAndPassword = parts[1].split(":");
            proxy = proxyFactory.createAuthProxy(address, port, usernameAndPassword[0], usernameAndPassword[1]);
        }
        return proxy;
    }
}
