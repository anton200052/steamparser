package me.vasylkov.steamparser.apache_client.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.apache_client.model.ApacheClientProxyWrapper;
import me.vasylkov.steamparser.apache_client.model.ApacheClientWrapper;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class  ApacheClientAndProxyChanger {
    private final ParsingProperties parsingProperties;
    private final ApacheClientFactory apacheClientFactory;
    private final ApacheClientProxyManager apacheClientProxyManager;
    private final Logger logger;

    public void changeProxyAndApacheClient(ApacheClientWrapper apacheClientWrapper) {
        if (parsingProperties.isEnableProxy()) {
            ApacheClientProxyWrapper apacheClientProxyWrapperLast = apacheClientWrapper.getApacheClientProxyWrapper();

            if (apacheClientProxyWrapperLast != null) {
                apacheClientProxyManager.blockProxy(apacheClientProxyWrapperLast);
            }

            ApacheClientProxyWrapper availableProxyWrapper = apacheClientProxyManager.getAvailableProxy();
            ApacheClientWrapper newApacheClientWrapper = null;

            if (apacheClientProxyWrapperLast != null || availableProxyWrapper != null) {
                if (availableProxyWrapper != null) {
                    // Если доступен прокси, создаем клиент с ним
                    newApacheClientWrapper = apacheClientFactory.createApacheClientWrapper(availableProxyWrapper);
                }
                else {
                    // Если прокси недоступен, создаем обычный клиент
                    newApacheClientWrapper = apacheClientFactory.createApacheClientWrapper();
                }

                closeOldClient(apacheClientWrapper.getCloseableHttpClient());

                apacheClientWrapper.setApacheClientProxyWrapper(newApacheClientWrapper.getApacheClientProxyWrapper());
                apacheClientWrapper.setCloseableHttpClient(newApacheClientWrapper.getCloseableHttpClient());
            }
        }
    }

    private void closeOldClient(CloseableHttpClient oldHttpClient) {
        if (oldHttpClient != null) {
            try {
                oldHttpClient.close();
            }
            catch (IOException e) {
                logger.error("Ошибка закрытия HTTP клиента!", e.getMessage());
            }
        }
    }
}
