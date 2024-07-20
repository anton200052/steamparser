package me.vasylkov.steamparser.common.abstraction;

public interface ProxyWrapper<T>
{
    T getProxy();
    boolean isBlocked();
}
