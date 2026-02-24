package me.vasylkov.steamparser.apache_client.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.vasylkov.steamparser.apache_client.model.ApacheClientProxy;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.DelayQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApacheClientProxyManager
{
    private final DelayQueue<ApacheClientProxy> proxyQueue = new DelayQueue<>();
    private final StringToApacheClientProxyConverter proxyConverter;
    private final ParsingProperties parsingProperties;

    public ApacheClientProxy borrowProxy() throws InterruptedException {
        return proxyQueue.take();
    }

    public void returnProxy(ApacheClientProxy proxy, long cooldownMilliseconds) {
        proxy.setCooldown(cooldownMilliseconds);
        proxyQueue.offer(proxy);
    }

    @PostConstruct
    public void initProxyQueue() {
        if (!parsingProperties.isEnableProxy()) {
            log.info("Proxy usage is disabled in properties.");
            return;
        }

        if (parsingProperties.getProxyList() == null || parsingProperties.getProxyList().isEmpty()) {
            log.warn("Proxy is enabled, but the proxy list is empty!");
            return;
        }

        parsingProperties.getProxyList().stream()
                .map(proxyConverter::convert)
                .filter(Objects::nonNull)
                .forEach(proxyQueue::offer);

        log.info("Successfully loaded {} proxies into the DelayQueue.", proxyQueue.size());
    }
}
