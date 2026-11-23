package com.example; 

import java.util.Objects;

public class Item implements ItemType{

    private final String name;
    private String type;
    private boolean available;

    public Item(String name, String type, boolean available) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name cannot be null or blank");
        }
        this.name = name.trim();
        this.type = (type != null && !type.isBlank()) ? type.trim() : "General";
        this.available = available;
    }
    @Override 
    public String getName() {
        return name;
    }
    @Override 
    public String getType() {
        return type;
    }
    @Override 
    public void setType(String type) {
        this.type = (type != null && !type.isBlank()) ? type.trim() : "General";
    }
    @Override 
    public boolean isAvailable() {
        return available;
    }
    @Override 
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getItem() {
        return "Name: " + name + ", Type: " + type + ", Available: " + available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return name.equalsIgnoreCase(item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return getItem();
    }
}