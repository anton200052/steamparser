package me.vasylkov.steamparser.apache_client.component;

import me.vasylkov.steamparser.apache_client.model.ProxyType;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ProxyValidator
{
    private final String proxyWithAuthRegex = "^([a-zA-Z0-9.-]+):(\\d{1,5})@([^:]+):(.+)$";
    private final String proxyWithoutAuthRegex = "^([a-zA-Z0-9.-]+):(\\d{1,5})$";

    private final Pattern proxyWithAuthPattern = Pattern.compile(proxyWithAuthRegex);
    private final Pattern proxyWithoutAuthPattern = Pattern.compile(proxyWithoutAuthRegex);

    public ProxyType getProxyType(String proxy)
    {
        Matcher withAuthMatcher = proxyWithAuthPattern.matcher(proxy);
        if (withAuthMatcher.matches() && isValidPort(withAuthMatcher.group(2)))
        {
            return ProxyType.AUTHENTICATION;
        }

        Matcher withoutAuthMatcher = proxyWithoutAuthPattern.matcher(proxy);
        if (withoutAuthMatcher.matches() && isValidPort(withoutAuthMatcher.group(2)))
        {
            return ProxyType.DEFAULT;
        }

        return ProxyType.INVALID;
    }

    private boolean isValidPort(String portStr) {
        try {
            int port = Integer.parseInt(portStr);
            return port > 0 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
