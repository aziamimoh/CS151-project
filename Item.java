public class Item {
    private String name;
    private String type;
    private double cost;
    private boolean available;

    public Item(String name, String type, double cost, boolean available) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.available = available;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getItem() {
        return "Name: " + name + ", Type: " + type + ", Cost: " + cost + ", Available: " + available;
    }



    public static void main(String[] args) throws Exception {
   
        Item hammer = new Item("Hammer", "Tool", 12.5, true);
        System.out.println(hammer.getItem());
    }

}
