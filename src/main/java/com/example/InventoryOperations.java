package com.example;

import java.util.List;

  //Operations supported by the restaurant inventory
  //Inventory implements this interface

public interface InventoryOperations {
    void addToInventory(Item item, int quantity);

    boolean adjustStock(String itemName, int delta);

    void changeQuantity(Item item, int newQuantity);

    int getQuantity(Item item);

    int getQuantityByName(String name);

    Item searchByName(String name);

    List<Item> searchByType(String type);

    List<Item> getLowStockItems(int threshold);

    List<Item> getItems();

    int size();
}
