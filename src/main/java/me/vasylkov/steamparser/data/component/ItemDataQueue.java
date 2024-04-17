package me.vasylkov.steamparser.data.component;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.entity.ItemData;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
@Data
public class ItemDataQueue
{
    private BlockingQueue<ItemData> queue = new LinkedBlockingQueue<>();


    public boolean isEmpty()
    {
        return queue.isEmpty();
    }

    public ItemData getNext()
    {
        return queue.poll();
    }

    public void addToQueue(ItemData item)
    {
        queue.offer(item);
    }
}
