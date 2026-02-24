package me.vasylkov.steamparser.apache_client.model;

import lombok.Data;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
public class ApacheClientProxy implements Delayed
{
    private BasicCredentialsProvider basicCredentialsProvider;
    private HttpHost httpHost;
    private long readyTime;

    public ApacheClientProxy(BasicCredentialsProvider basicCredentialsProvider, HttpHost httpHost) {
        this.basicCredentialsProvider = basicCredentialsProvider;
        this.httpHost = httpHost;
        this.readyTime = System.currentTimeMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = readyTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
    }

    public void setCooldown(long cooldownMillis) {
        this.readyTime = System.currentTimeMillis() + cooldownMillis;
    }
}
