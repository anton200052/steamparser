package me.vasylkov.steamparser.common.abstraction;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.auth.AuthType;
import org.openqa.selenium.Proxy;

import java.net.InetSocketAddress;

public interface ProxyFactory<T extends ProxyWrapper<?>>
{
    T createDefaultProxy(String host, int port);
    T createAuthProxy(String host, int port, String username, String password);
}
