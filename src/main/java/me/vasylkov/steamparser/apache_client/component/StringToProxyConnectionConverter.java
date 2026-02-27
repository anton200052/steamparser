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
public class  StringToProxyConnectionConverter implements Converter<String, ClientConnection> {
    private final ProxyValidator proxyValidator;
    private final ProxyConnectionFactory proxyFactory;

    @Override
    public ClientConnection convert(String source) {

        ProxyType proxyType = proxyValidator.getProxyType(source);
        if (proxyType == ProxyType.INVALID) {
            log.error("Proxy {} in wrong format, this proxy will not be used.", source);
            return null;
        }

        int atIndex = source.indexOf('@');
        String hostPort = (proxyType == ProxyType.DEFAULT) ? source : source.substring(0, atIndex);

        int colonIndexHost = hostPort.indexOf(':');
        String address = hostPort.substring(0, colonIndexHost);
        int port = Integer.parseInt(hostPort.substring(colonIndexHost + 1));

        ClientConnection proxy;
        if (proxyType == ProxyType.DEFAULT) {
            proxy = proxyFactory.createDefaultProxy(address, port);
        } else {
            String credentials = source.substring(atIndex + 1);
            int colonIndexCreds = credentials.indexOf(':');
            String username = credentials.substring(0, colonIndexCreds);
            String password = credentials.substring(colonIndexCreds + 1);
            proxy = proxyFactory.createAuthProxy(address, port, username, password);
        }
        return proxy;
    }
}
