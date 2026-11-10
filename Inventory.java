package restaurant;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


 //Manages the restaurant's inventory of items and their quantities
public class Inventory {

    private final Map<Item, Integer> stock;

    // Used by JavaFX TableView/ListView to show inventory data
    private final ObservableList<InventoryItem> observableItems;

      //Creates an empty inventory
     
    public Inventory() {
        stock = new LinkedHashMap<>();
        observableItems = FXCollections.observableArrayList();
    }

    
    //Returns the observable inventory list for JavaFX UI components
     
    public ObservableList<InventoryItem> getObservableItems() {
        return observableItems;
    }

     //Adds an item to the inventory
     //If the item already exists, its quantity is increased
     
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

    
     //Increases or decreases the stock of an item using its name
     
     //delta=amount to add or subtract
     //return true if the stock was adjusted successfully
     //false if the item does not exist or the result would be negative
     
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

    
     //Returns the quantity of an item.
     //Returns 0 if the item is not in the inventory.
     
     public int getQuantity(Item item) {
        if (item == null) {
            return 0;
        }
    
        Item existingItem = searchByName(item.getName());
    
        if (existingItem == null) {
            return 0;
        }
    
        return stock.get(existingItem);
    }

    
      //Returns the quantity of an item using its name
      //Returns 0 if the item cannot be found
     
    public int getQuantityByName(String name) {
        Item item = searchByName(name);

        if (item == null) {
            return 0;
        }

        return stock.get(item);
    }

    
     //Searches for an item by name and is case-insensitive
    
    public Item searchByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        String searchName = name.trim();

        for (Item item : stock.keySet()) {
            if (item.getName().equalsIgnoreCase(searchName)) {
                return item;
            }
        }

        return null;
    }

      //Searches for all inventory items of a specific type
      //It is case-insensitive
     
    public List<Item> searchByType(String type) {
        List<Item> results = new ArrayList<>();

        if (type == null || type.isBlank()) {
            return results;
        }

        String searchType = type.trim();

        for (Item item : stock.keySet()) {
            if (item.getType().equalsIgnoreCase(searchType)) {
                results.add(item);
            }
        }

        return results;
    }

    
      //Returns all items whose quantity is less than or equal
      //to the specified threshold
     
      //Example:
      //getLowStockItems(5) returns items with 5 or fewer units
     
    public List<Item> getLowStockItems(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException(
                    "Threshold cannot be negative: " + threshold
            );
        }

        List<Item> lowStockItems = new ArrayList<>();

        for (Map.Entry<Item, Integer> entry : stock.entrySet()) {
            if (entry.getValue() <= threshold) {
                lowStockItems.add(entry.getKey());
            }
        }

        return lowStockItems;
    }

      //Returns an unmodifiable list containing all inventory items
     
    public List<Item> getItems() {
        return Collections.unmodifiableList(
                new ArrayList<>(stock.keySet())
        );
    }

    
      //Returns the number of different items in the inventory
     
    public int size() {
        return stock.size();
    }

    
      //Finds the InventoryItem wrapper associated with an Item
      //Used to keep the JavaFX ObservableList synchronized with stock
     
      private InventoryItem findWrapper(Item item) {
        if (item == null) {
            return null;
        }
    
        for (InventoryItem entry : observableItems) {
            if (entry.getItem().getName().equalsIgnoreCase(item.getName())) {
                return entry;
            }
        }
    
        return null;
    }
}
