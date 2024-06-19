package me.vasylkov.steamparser.selenium.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.selenium.configuration.SeleniumProperties;
import me.vasylkov.steamparser.selenium.entity.ProxyWrapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ProxyManager
{
    private List<ProxyWrapper> proxyList;
    private final ProxyListConverter proxyListConverter;
    private final SeleniumProperties seleniumProperties;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public synchronized void blockProxy(ProxyWrapper proxyWrapper)
    {
        if (!proxyWrapper.isBlocked())
        {
            proxyWrapper.setBlocked(true);
            scheduler.schedule(() -> unblockProxy(proxyWrapper), seleniumProperties.getProxiesBlockingTime(), TimeUnit.SECONDS);
        }
    }

    private synchronized void unblockProxy(ProxyWrapper proxyWrapper)
    {
        if (proxyWrapper.isBlocked())
        {
            proxyWrapper.setBlocked(false);
        }
    }

    public synchronized ProxyWrapper getAvailableProxy()
    {
        for (ProxyWrapper proxyWrapper : proxyList)
        {
            if (!proxyWrapper.isBlocked())
            {
                return proxyWrapper;
            }
        }
        return null;
    }

    @PostConstruct
    public void initProxyList()
    {
        if (seleniumProperties.isProxiesEnabled())
        {
            proxyList = proxyListConverter.convert(seleniumProperties.getProxyList());
        }
    }
}
