package me.vasylkov.steamparser.selenium.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.selenium.entity.SeleniumProxyWrapper;
import me.vasylkov.steamparser.common.component.ProxyValidator;
import me.vasylkov.steamparser.common.enums.ProxyType;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeleniumProxyListConverter implements Converter<List<String>, List<SeleniumProxyWrapper>>
{
    private final ProxyValidator proxyValidator;
    private final SeleniumProxyFactory proxyManager;
    private final Logger logger;

    @Override
    public List<SeleniumProxyWrapper> convert(List<String> source)
    {
        List<SeleniumProxyWrapper> proxies = new ArrayList<>();
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

            SeleniumProxyWrapper proxy;
            if (proxyType == ProxyType.DEFAULT)
            {
                proxy = proxyManager.createDefaultProxy(address, port);
            }
            else
            {
                String[] usernameAndPassword = parts[1].split(":");
                proxy = proxyManager.createAuthProxy(address, port, usernameAndPassword[0], usernameAndPassword[1]);
            }
            proxies.add(proxy);
        }
        return proxies;
    }
}
