package me.vasylkov.steamparser.selenium.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.common.abstraction.ProxyManager;
import me.vasylkov.steamparser.selenium.entity.SeleniumProxyWrapper;
import me.vasylkov.steamparser.selenium.configuration.SeleniumProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SeleniumProxyManager implements ProxyManager<SeleniumProxyWrapper>
{
    private List<SeleniumProxyWrapper> proxyList;
    private final SeleniumProxyListConverter seleniumProxyListConverter;
    private final SeleniumProperties seleniumProperties;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public synchronized void blockProxy(SeleniumProxyWrapper seleniumProxyWrapper)
    {
        if (!seleniumProxyWrapper.isBlocked())
        {
            seleniumProxyWrapper.setBlocked(true);
            scheduler.schedule(() -> unblockProxy(seleniumProxyWrapper), seleniumProperties.getProxiesBlockingTime(), TimeUnit.SECONDS);
        }
    }

    @Override
    public synchronized void unblockProxy(SeleniumProxyWrapper seleniumProxyWrapper)
    {
        if (seleniumProxyWrapper.isBlocked())
        {
            seleniumProxyWrapper.setBlocked(false);
        }
    }

    @Override
    public synchronized SeleniumProxyWrapper getAvailableProxy()
    {
        for (SeleniumProxyWrapper seleniumProxyWrapper : proxyList)
        {
            if (!seleniumProxyWrapper.isBlocked())
            {
                return seleniumProxyWrapper;
            }
        }
        return null;
    }

    @PostConstruct
    public void initProxyList()
    {
        if (seleniumProperties.isProxiesEnabled())
        {
            proxyList = seleniumProxyListConverter.convert(seleniumProperties.getProxyList());
        }
    }
}
