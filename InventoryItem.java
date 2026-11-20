package com.example;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


  //Presentation wrapper for JavaFX TableView bindings and Iterator traversal.

public class InventoryItem {

    private final Item item;
    private final IntegerProperty quantity;

    public InventoryItem(Item item, int initialQuantity) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("Initial quantity cannot be negative");
        }
        this.item = item;
        this.quantity = new SimpleIntegerProperty(initialQuantity);
    }

    public Item getItem() {
        return item;
    }

    public String getName() {
        return item.getName();
    }

    public String getType() {
        return item.getType();
    }

    public boolean isAvailable() {
        return item.isAvailable();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        quantity.set(qty);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    @Override
    public String toString() {
        return item.getName() + " (Qty: " + getQuantity() + ")";
    }
}