package me.vasylkov.steamparser.data.component;

import me.vasylkov.steamparser.data.entity.Item;

public interface ItemQueue<T extends Item>
{
    boolean isEmpty();

    T getNext();

    void addToQueue(T item);
}
