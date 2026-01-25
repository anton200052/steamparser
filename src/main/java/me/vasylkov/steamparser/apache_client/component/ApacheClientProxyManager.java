package me.vasylkov.steamparser.apache_client.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.apache_client.model.ApacheClientProxy;
import me.vasylkov.steamparser.apache_client.model.ApacheClientProxyWrapper;
 import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ApacheClientProxyManager implements
{
    private List<ApacheClientProxy> proxyList;
    private final ApacheClientProxyListConverter apacheClientProxyListConverter;
    private final ParsingProperties parsingProperties;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public synchronized void blockProxy(ApacheClientProxy proxy)
    {
        if (!proxy.isBlocked())
        {
            proxy.setBlocked(true);
            scheduler.schedule(() -> unblockProxy(proxy), parsingProperties.getProxyBlockingTime(), TimeUnit.SECONDS);
        }
    }

    @Override
    public synchronized void unblockProxy(ApacheClientProxyWrapper proxyWrapper)
    {
        if (proxyWrapper.isBlocked())
        {
            proxyWrapper.setBlocked(false);
        }
    }

    @Override
    public synchronized ApacheClientProxyWrapper getAvailableProxy()
    {
        for (ApacheClientProxyWrapper proxyWrapper : proxyList)
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
        if (parsingProperties.isEnableProxy())
        {
            proxyList = apacheClientProxyListConverter.convert(parsingProperties.getProxyList());
        }
    }
}
