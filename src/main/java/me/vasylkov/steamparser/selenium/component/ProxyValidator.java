package me.vasylkov.steamparser.selenium.component;

import me.vasylkov.steamparser.selenium.entity.ProxyType;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ProxyValidator
{
    private final String proxyWithAuthRegex = "^(\\d{1,3}\\.){3}\\d{1,3}:\\d{1,5}@\\w+:\\w+$";
    private final String proxyWithoutAuthRegex = "^(\\d{1,3}\\.){3}\\d{1,3}:\\d{1,5}$";

    private final Pattern proxyWithAuthPattern = Pattern.compile(proxyWithAuthRegex);
    private final Pattern proxyWithoutAuthPattern = Pattern.compile(proxyWithoutAuthRegex);

    public ProxyType getProxyType(String proxy)
    {
        if (proxyWithAuthPattern.matcher(proxy).matches())
        {
            return ProxyType.AUTHENTICATION;
        }
        else if (proxyWithoutAuthPattern.matcher(proxy).matches())
        {
            return ProxyType.DEFAULT;
        }
        else
        {
            return ProxyType.INVALID;
        }
    }
}
