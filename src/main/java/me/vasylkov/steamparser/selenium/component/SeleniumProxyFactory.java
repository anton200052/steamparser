package me.vasylkov.steamparser.selenium.component;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.common.abstraction.ProxyFactory;
import me.vasylkov.steamparser.selenium.entity.SeleniumProxyWrapper;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.auth.AuthType;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeleniumProxyFactory implements ProxyFactory<SeleniumProxyWrapper>
{
    private final Logger logger;
    private final List<BrowserMobProxy> proxiesNeedToStop = new ArrayList<>();

    @Override
    public SeleniumProxyWrapper createDefaultProxy(String host, int port)
    {
        String proxyStr = host + ":" + port;

        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyStr);
        proxy.setSslProxy(proxyStr);

        logger.info("Добавлено прокси без аутентификации с адресом {}", host + ":" + port);
        return new SeleniumProxyWrapper(proxy, false);
    }

    @Override
    public SeleniumProxyWrapper createAuthProxy(String host, int port, String username, String password)
    {
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.setChainedProxy(new InetSocketAddress(host, port));
        proxy.chainedProxyAuthorization(username, password, AuthType.BASIC);
        proxy.start(0);

        proxiesNeedToStop.add(proxy);
        logger.info("Добавлено прокси с аутентификацией с адресом {}", host + ":" + port);
        return new SeleniumProxyWrapper(ClientUtil.createSeleniumProxy(proxy), false);
    }

    @PreDestroy
    public void stopAllProxies()
    {
        for (BrowserMobProxy proxy : proxiesNeedToStop)
        {
            if (proxy.isStarted())
            {
                proxy.stop();
            }
        }
    }
}
