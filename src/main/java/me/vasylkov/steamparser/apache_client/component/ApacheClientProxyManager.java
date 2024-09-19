package me.vasylkov.steamparser.apache_client.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.apache_client.entity.ApacheClientProxyWrapper;
import me.vasylkov.steamparser.common.abstraction.ProxyManager;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ApacheClientProxyManager implements ProxyManager<ApacheClientProxyWrapper>
{
    private List<ApacheClientProxyWrapper> proxyList;
    private final ApacheClientProxyListConverter apacheClientProxyListConverter;
    private final ParsingProperties parsingProperties;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public synchronized void blockProxy(ApacheClientProxyWrapper proxyWrapper)
    {
        if (!proxyWrapper.isBlocked())
        {
            proxyWrapper.setBlocked(true);
            scheduler.schedule(() -> unblockProxy(proxyWrapper), parsingProperties.getProxyBlockingTime(), TimeUnit.SECONDS);
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
