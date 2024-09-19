package me.vasylkov.steamparser.apache_client.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.apache_client.entity.ApacheClientProxyWrapper;
import me.vasylkov.steamparser.apache_client.entity.ApacheClientWrapper;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApacheClientAndProxyChanger
{
    private final ParsingProperties parsingProperties;
    private final ApacheClientFactory apacheClientFactory;
    private final ApacheClientProxyManager apacheClientProxyManager;

    public void changeProxyAndApacheClient(ApacheClientWrapper apacheClientWrapper)
    {
        if (parsingProperties.isEnableProxy())
        {
            ApacheClientProxyWrapper apacheClientProxyWrapperLast = apacheClientWrapper.getApacheClientProxyWrapper();

            if (apacheClientProxyWrapperLast != null)
            {
                apacheClientProxyManager.blockProxy(apacheClientProxyWrapperLast);
            }

            ApacheClientProxyWrapper availableProxyWrapper = apacheClientProxyManager.getAvailableProxy();
            ApacheClientWrapper newApacheClientWrapper = null;

            if (apacheClientProxyWrapperLast != null || availableProxyWrapper != null)
            {
                if (availableProxyWrapper != null)
                {
                    // Если доступен прокси, создаем клиент с ним
                    newApacheClientWrapper = apacheClientFactory.createApacheClientWrapper(availableProxyWrapper);
                }
                else
                {
                    // Если прокси недоступен, создаем обычный клиент
                    newApacheClientWrapper = apacheClientFactory.createApacheClientWrapper();
                }

                apacheClientWrapper.setApacheClientProxyWrapper(newApacheClientWrapper.getApacheClientProxyWrapper());
                apacheClientWrapper.setCloseableHttpClient(newApacheClientWrapper.getCloseableHttpClient());
            }
        }
    }
}
