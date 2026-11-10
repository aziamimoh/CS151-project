package restaurant;

import javafx.beans.property.*;


  //Presentation wrapper for JavaFX TableView bindings
 
public class InventoryItem {

    private final Item item;
    private final IntegerProperty quantity;

    public InventoryItem(Item item, int initialQuantity) {
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

    public double getCost() {
        return item.getCost();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int qty) {
        quantity.set(qty);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }
}
