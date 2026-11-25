package com.example;

  //Defines the common interface for traversing inventory items
  //using the Iterator Design Pattern
 
public interface InventoryIterator {

    InventoryItem first();

    InventoryItem next();

    InventoryItem currentItem();

    boolean isDone();
}