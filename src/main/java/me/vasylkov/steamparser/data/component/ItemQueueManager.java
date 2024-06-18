package me.vasylkov.steamparser.data.component;

import me.vasylkov.steamparser.data.entity.Item;

public interface ItemQueueManager<T extends Item>
{
    T getNextAvailable();

    void addItem(T item);
    void moveToLast(T item);
}
