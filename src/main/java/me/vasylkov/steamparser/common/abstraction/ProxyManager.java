package me.vasylkov.steamparser.common.abstraction;

public interface ProxyManager<T extends ProxyWrapper<?>>
{
    void blockProxy(T proxyWrapper);

    void unblockProxy(T proxyWrapper);

    T getAvailableProxy();
}
