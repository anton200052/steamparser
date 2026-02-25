package me.vasylkov.steamparser.apache_client.model;

import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
public abstract class ClientConnection implements Delayed {
    protected long readyTime;

    public ClientConnection() {
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
