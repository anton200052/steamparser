package me.vasylkov.steamparser.data.component;

import me.vasylkov.steamparser.data.entity.Item;

public interface ItemQueueManager<T extends Item>
{
    T getAndBlockFirstAvailableItem();

    void addItem(T item);
    void moveItemToLastAndUnblock(T item);
}
