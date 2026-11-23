package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

  //Manages the restaurant's inventory of items and their quantities
  //Implements the Iterator Pattern (Iterable<InventoryItem>) to allow sequential
  //traversal without exposing the internal Map or ObservableList
 
public class Inventory
implements InventoryOperations, Iterable<InventoryItem> {

private final Map<Item, Integer> stock;
private final ObservableList<InventoryItem> observableItems;

// Creates an empty inventory
public Inventory() {
  this.stock = new LinkedHashMap<>();
  this.observableItems = FXCollections.observableArrayList();
}

//Returns a read-only iterator over the inventory

@Override
public Iterator<InventoryItem> iterator() {
  return Collections.unmodifiableList(observableItems).iterator();
}

 //Returns an iterator containing only items at or below
 //the specified stock threshold

public Iterator<InventoryItem> lowStockIterator(int threshold) {
  if (threshold < 0) {
      throw new IllegalArgumentException(
              "Threshold cannot be negative: " + threshold
      );
  }

  List<InventoryItem> filtered = new ArrayList<>();

  for (InventoryItem entry : this) {
      if (entry.getQuantity() <= threshold) {
          filtered.add(entry);
      }
  }

  return Collections.unmodifiableList(filtered).iterator();
}

 //Gives a read-only observable list for JavaFX components

public ObservableList<InventoryItem> getObservableItems() {
  return FXCollections.unmodifiableObservableList(observableItems);
}

 //Adds an item to the inventory
 //If the item already exists, its quantity is increased

@Override
public void addToInventory(Item item, int quantity) {
  if (item == null) {
      throw new IllegalArgumentException("Item cannot be null");
  }

  if (quantity < 0) {
      throw new IllegalArgumentException(
              "Quantity cannot be negative: " + quantity
      );
  }

  Item existingItem = searchByName(item.getName());

  if (existingItem != null) {
      int updatedQty = stock.get(existingItem) + quantity;
      stock.put(existingItem, updatedQty);

      InventoryItem wrapper = findWrapper(existingItem);

      if (wrapper != null) {
          wrapper.setQuantity(updatedQty);
      }

  } else {
      stock.put(item, quantity);
      observableItems.add(new InventoryItem(item, quantity));
  }
}

 //Increases or decreases an item's stock
 //Returns false if the item does not exist or if the
 //adjustment would cause the quantity to become negative

@Override
public boolean adjustStock(String itemName, int delta) {
  Item item = searchByName(itemName);

  if (item == null) {
      return false;
  }

  int currentQty = stock.get(item);
  int newQty = currentQty + delta;

  if (newQty < 0) {
      return false;
  }

  stock.put(item, newQty);

  InventoryItem wrapper = findWrapper(item);

  if (wrapper != null) {
      wrapper.setQuantity(newQty);
  }

  return true;
}

//Sets the quantity of an existing inventory item

@Override
public void changeQuantity(Item item, int newQuantity) {
  if (item == null) {
      throw new IllegalArgumentException("Item cannot be null");
  }

  if (newQuantity < 0) {
      throw new IllegalArgumentException(
              "Quantity cannot be negative: " + newQuantity
      );
  }

  Item existingItem = searchByName(item.getName());

  if (existingItem == null) {
      throw new IllegalArgumentException(
              "Item does not exist in inventory"
      );
  }

  stock.put(existingItem, newQuantity);

  InventoryItem wrapper = findWrapper(existingItem);

  if (wrapper != null) {
      wrapper.setQuantity(newQuantity);
  }
}

//Returns an item's current quantity
//Returns 0 if the item is not in the inventory

@Override
public int getQuantity(Item item) {
  if (item == null) {
      return 0;
  }

  Item existingItem = searchByName(item.getName());

  return (existingItem == null)
          ? 0
          : stock.get(existingItem);
}

 //Returns an item's quantity using its name
 //Returns 0 if the item cannot be found

@Override
public int getQuantityByName(String name) {
  Item item = searchByName(name);

  return (item == null)
          ? 0
          : stock.get(item);
}

 //Searches for an item by name and is case-insensitive

@Override
public Item searchByName(String name) {
  if (name == null || name.isBlank()) {
      return null;
  }

  String searchName = name.trim();

  for (InventoryItem entry : this) {
      if (entry.getName().equalsIgnoreCase(searchName)) {
          return entry.getItem();
      }
  }

  return null;
}

//Returns all inventory items of the specified type
//The search is case-insensitive

@Override
public List<Item> searchByType(String type) {
  List<Item> results = new ArrayList<>();

  if (type == null || type.isBlank()) {
      return results;
  }

  String searchType = type.trim();

  for (InventoryItem entry : this) {
      if (entry.getType().equalsIgnoreCase(searchType)) {
          results.add(entry.getItem());
      }
  }

  return results;
}

//Returns all items whose quantity is less than or equal
//to the specified threshold

@Override
public List<Item> getLowStockItems(int threshold) {
  if (threshold < 0) {
      throw new IllegalArgumentException(
              "Threshold cannot be negative: " + threshold
      );
  }

  List<Item> lowStockItems = new ArrayList<>();
  Iterator<InventoryItem> it = lowStockIterator(threshold);

  while (it.hasNext()) {
      lowStockItems.add(it.next().getItem());
  }

  return lowStockItems;
}

 //Returns a read-only list containing all inventory items

@Override
public List<Item> getItems() {
  List<Item> items = new ArrayList<>();

  for (InventoryItem entry : this) {
      items.add(entry.getItem());
  }

  return Collections.unmodifiableList(items);
}

//Returns the number of different items in the inventory

@Override
public int size() {
  return stock.size();
}

//Finds the InventoryItem wrapper associated with an Item
//Used internally to keep the JavaFX ObservableList
//synchronized with the stock Map

private InventoryItem findWrapper(Item item) {
  if (item == null) {
      return null;
  }

  for (InventoryItem entry : this) {
      if (entry.getName().equalsIgnoreCase(item.getName())) {
          return entry;
      }
  }

  return null;
}
}