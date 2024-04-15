package me.vasylkov.steamparser.parsing.component;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.httpclient.entity.ItemToParseData;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
@Data
public class ItemsStorage
{
    private BlockingQueue<ItemToParseData> queue = new LinkedBlockingQueue<>();


    public boolean isEmpty()
    {
        return queue.isEmpty();
    }

    public ItemToParseData getNext()
    {
        return queue.poll();
    }

    public void addToQueue(ItemToParseData item)
    {
        queue.offer(item);
    }
}
