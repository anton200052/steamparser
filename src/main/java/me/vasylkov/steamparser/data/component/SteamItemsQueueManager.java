package me.vasylkov.steamparser.data.component;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.data.entity.SteamItem;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@RequiredArgsConstructor
@Data
public class SteamItemsQueueManager implements ItemQueueManager<SteamItem>
{
    private ConcurrentLinkedDeque<SteamItem> items = new ConcurrentLinkedDeque<>();

    @Override
    public synchronized SteamItem getAndBlockFirstAvailableItem()
    {
        for (SteamItem item : items)
        {
            if (item.isAvailable())
            {
                item.setAvailable(false);
                return item;
            }
        }
        return null;
    }

    @Override
    public synchronized void addItem(SteamItem item)
    {
        items.add(item);
    }

    @Override
    public synchronized void moveItemToLastAndUnblock(SteamItem item)
    {
        item.setAvailable(true);
        items.remove(item);
        items.addLast(item);
    }
}
