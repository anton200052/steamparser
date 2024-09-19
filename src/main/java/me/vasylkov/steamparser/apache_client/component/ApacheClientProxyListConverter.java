package me.vasylkov.steamparser.apache_client.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.apache_client.entity.ApacheClientProxyWrapper;
import me.vasylkov.steamparser.common.component.ProxyValidator;
import me.vasylkov.steamparser.common.enums.ProxyType;
import org.slf4j.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApacheClientProxyListConverter implements Converter<List<String>, List<ApacheClientProxyWrapper>>
{
    private final ProxyValidator proxyValidator;
    private final ApacheClientProxyFactory proxyFactory;
    private final Logger logger;

    @Override
    public List<ApacheClientProxyWrapper> convert(List<String> source)
    {
        List<ApacheClientProxyWrapper> proxies = new ArrayList<>();
        logger.info("Добавляем прокси.");

        for (String proxyString : source)
        {
            ProxyType proxyType = proxyValidator.getProxyType(proxyString);
            if (proxyType == ProxyType.INVALID)
            {
                logger.error("Адрес прокси {} указан в ошибочном формате, прокси добавлен не будет.", proxyString);
                continue;
            }

            String[] parts = proxyString.split("@");
            String[] ipAndPort = parts[0].split(":");

            String address = ipAndPort[0];
            int port = Integer.parseInt(ipAndPort[1]);

            ApacheClientProxyWrapper proxy;
            if (proxyType == ProxyType.DEFAULT)
            {
                proxy = proxyFactory.createDefaultProxy(address, port);
            }
            else
            {
                String[] usernameAndPassword = parts[1].split(":");
                proxy = proxyFactory.createAuthProxy(address, port, usernameAndPassword[0], usernameAndPassword[1]);
            }
            proxies.add(proxy);
        }
        return proxies;
    }
}
