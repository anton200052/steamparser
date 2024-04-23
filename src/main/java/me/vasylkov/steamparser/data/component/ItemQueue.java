package me.vasylkov.steamparser.data.component;

public interface ItemQueue<T>
{
    boolean isEmpty();

    T getNext();

    void addToQueue(T item);
}
