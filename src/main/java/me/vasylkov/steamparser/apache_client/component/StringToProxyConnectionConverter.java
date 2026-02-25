package me.vasylkov.steamparser.apache_client.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.vasylkov.steamparser.apache_client.model.ClientConnection;
import me.vasylkov.steamparser.apache_client.model.ProxyType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StringToProxyConnectionConverter implements Converter<String, ClientConnection> {
    private final ProxyValidator proxyValidator;
    private final ProxyConnectionFactory proxyFactory;

    @Override
    public ClientConnection convert(String source) {

        ProxyType proxyType = proxyValidator.getProxyType(source);
        if (proxyType == ProxyType.INVALID) {
            log.error("Proxy {} in wrong format, this proxy will not be used.", source);
            return null;
        }

        String[] parts = source.split("@");
        String[] ipAndPort = parts[0].split(":");

        String address = ipAndPort[0];
        int port = Integer.parseInt(ipAndPort[1]);

        ClientConnection proxy;
        if (proxyType == ProxyType.DEFAULT) {
            proxy = proxyFactory.createDefaultProxy(address, port);
        } else {
            String[] usernameAndPassword = parts[1].split(":");
            proxy = proxyFactory.createAuthProxy(address, port, usernameAndPassword[0], usernameAndPassword[1]);
        }
        return proxy;
    }
}
