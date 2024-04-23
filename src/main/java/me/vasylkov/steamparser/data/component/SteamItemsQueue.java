package me.vasylkov.steamparser.data.component;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.entity.SteamItem;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
@Data
public class SteamItemsQueue implements ItemQueue<SteamItem>
{
    private BlockingQueue<SteamItem> queue = new LinkedBlockingQueue<>();


    @Override
    public boolean isEmpty()
    {
        return queue.isEmpty();
    }

    @Override
    public SteamItem getNext()
    {
        return queue.poll();
    }

    @Override
    public void addToQueue(SteamItem item)
    {
        queue.offer(item);
    }
}
