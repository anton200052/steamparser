package me.vasylkov.steamparser.data.component;

import me.vasylkov.steamparser.data.entity.Item;

public interface ItemQueueManager
{
    Item getAvailableOrLastItem(Item lastAvailable, boolean isParsingCycled);
    void addItem(Item item);
    void moveItemToLastAndUnblock(Item item);
    void updatePricesInQueue();
}
