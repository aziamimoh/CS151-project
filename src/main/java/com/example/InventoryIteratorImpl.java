package com.example;

import java.util.List;

// iterator that traverses InventoryItem objects
// Maintains a cursor to track the current position in the inventory

public class InventoryIteratorImpl implements InventoryIterator {

    private final List<InventoryItem> items;
    private int cursor;

    public InventoryIteratorImpl(List<InventoryItem> items) {
        this.items = items;
        this.cursor = 0;
    }

    @Override
    public InventoryItem first() {
        cursor = 0;

        if (items.isEmpty()) {
            return null;
        }

        return items.get(cursor);
    }

    @Override
    public InventoryItem next() {
        cursor++;

        if (isDone()) {
            return null;
        }

        return items.get(cursor);
    }

    @Override
    public InventoryItem currentItem() {
        if (isDone()) {
            return null;
        }

        return items.get(cursor);
    }

    @Override
    public boolean isDone() {
        return cursor >= items.size();
    }
}