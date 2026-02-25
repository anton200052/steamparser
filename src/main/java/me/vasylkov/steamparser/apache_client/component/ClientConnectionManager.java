package me.vasylkov.steamparser.apache_client.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.vasylkov.steamparser.apache_client.model.ClientConnection;
import me.vasylkov.steamparser.apache_client.model.LocalConnection;
import me.vasylkov.steamparser.properties.ParsingProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.DelayQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientConnectionManager
{
    private final DelayQueue<ClientConnection> connectionQueue = new DelayQueue<>();
    private final StringToProxyConnectionConverter proxyConverter;
    private final ParsingProperties parsingProperties;

    public ClientConnection borrowConnection() throws InterruptedException {
        return connectionQueue.take();
    }

    public void returnConnection(ClientConnection proxy, long cooldownMilliseconds) {
        proxy.setCooldown(cooldownMilliseconds);
        connectionQueue.offer(proxy);
    }

    @PostConstruct
    public void initConnectionQueue() {
        if (parsingProperties.isUseLocalConnection()) {
            connectionQueue.offer(new LocalConnection());
            log.info("Successfully loaded local connection into the DelayQueue.");
        }

        if (!parsingProperties.isEnableProxy()) {
            log.info("Proxy usage is disabled in properties.");
            checkQueueEmptiness();
            return;
        }

        if (parsingProperties.getProxyList() == null || parsingProperties.getProxyList().isEmpty()) {
            log.warn("Proxy is enabled, but the proxy list is empty!");
            checkQueueEmptiness();
            return;
        }

        parsingProperties.getProxyList().stream()
                .map(proxyConverter::convert)
                .filter(Objects::nonNull)
                .forEach(connectionQueue::offer);

        if (!connectionQueue.isEmpty()) {
            log.info("Successfully loaded {} proxy connections into the DelayQueue.",
                    connectionQueue.size() - (parsingProperties.isUseLocalConnection() ? 1 : 0));
        } else {
            log.warn("Proxy list contained only invalid entries.");
        }

        checkQueueEmptiness();
    }

    private void checkQueueEmptiness() {
        if (connectionQueue.isEmpty()) {
            log.error("CRITICAL: Connection queue is empty! Parser threads will block forever. Check your config (enable proxy or local connection).");
        }
    }
}
