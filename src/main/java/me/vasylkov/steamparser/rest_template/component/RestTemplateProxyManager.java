package me.vasylkov.steamparser.rest_template.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.common.abstraction.ProxyManager;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.rest_template.entity.RestTemplateProxyWrapper;
import me.vasylkov.steamparser.selenium.component.SeleniumProxyListConverter;
import me.vasylkov.steamparser.selenium.entity.SeleniumProxyWrapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RestTemplateProxyManager implements ProxyManager<RestTemplateProxyWrapper>
{
    private List<RestTemplateProxyWrapper> proxyList;
    private final RestTemplateProxyListConverter restTemplateProxyListConverter;
    private final ParsingProperties parsingProperties;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void blockProxy(RestTemplateProxyWrapper proxyWrapper)
    {
        if (!proxyWrapper.isBlocked())
        {
            proxyWrapper.setBlocked(true);
            scheduler.schedule(() -> unblockProxy(proxyWrapper), parsingProperties.getProxyBlockingTime(), TimeUnit.SECONDS);
        }
    }

    @Override
    public void unblockProxy(RestTemplateProxyWrapper proxyWrapper)
    {
        if (proxyWrapper.isBlocked())
        {
            proxyWrapper.setBlocked(false);
        }
    }

    @Override
    public RestTemplateProxyWrapper getAvailableProxy()
    {
        for (RestTemplateProxyWrapper proxyWrapper : proxyList)
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
            proxyList = restTemplateProxyListConverter.convert(parsingProperties.getProxyList());
        }
    }
}
