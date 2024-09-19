package me.vasylkov.steamparser.apache_client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.vasylkov.steamparser.common.abstraction.ProxyWrapper;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;

@Data
@AllArgsConstructor
public class ApacheClientProxyWrapper implements ProxyWrapper<ApacheClientProxy>
{
    private ApacheClientProxy proxy;
    private boolean blocked;
}
