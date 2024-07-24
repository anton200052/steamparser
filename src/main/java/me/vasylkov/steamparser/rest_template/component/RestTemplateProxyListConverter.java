package me.vasylkov.steamparser.rest_template.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.common.component.ProxyValidator;
import me.vasylkov.steamparser.common.enums.ProxyType;
import me.vasylkov.steamparser.rest_template.entity.RestTemplateProxyWrapper;
import me.vasylkov.steamparser.selenium.component.SeleniumProxyFactory;
import me.vasylkov.steamparser.selenium.entity.SeleniumProxyWrapper;
import org.slf4j.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RestTemplateProxyListConverter implements Converter<List<String>, List<RestTemplateProxyWrapper>>
{
    private final ProxyValidator proxyValidator;
    private final RestTemplateProxyFactory restTemplateProxyFactory;
    private final Logger logger;

    @Override
    public List<RestTemplateProxyWrapper> convert(List<String> source)
    {
        List<RestTemplateProxyWrapper> proxies = new ArrayList<>();
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

            RestTemplateProxyWrapper proxy;
            if (proxyType == ProxyType.DEFAULT)
            {
                proxy = restTemplateProxyFactory.createDefaultProxy(address, port);
            }
            else
            {
                String[] usernameAndPassword = parts[1].split(":");
                proxy = restTemplateProxyFactory.createAuthProxy(address, port, usernameAndPassword[0], usernameAndPassword[1]);
            }
            proxies.add(proxy);
        }
        return proxies;
    }
}
