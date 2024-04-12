package me.vasylkov.steamparser.parsing.component;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.httpclient.object.Item;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
@Data
public class ItemsStorage
{
    private BlockingQueue<Item> queue = new LinkedBlockingQueue<>();


    public boolean isEmpty()
    {
        return queue.isEmpty();
    }

    public Item getNext()
    {
        return queue.poll();
    }

    public void addToQueue(Item item)
    {
        queue.offer(item);
    }
}
