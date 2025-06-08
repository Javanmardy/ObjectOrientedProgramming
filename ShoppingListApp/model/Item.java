package model;

public class Item {
    private String name;
    private int count;
    private int price;

    public Item(String name, int count, int price) {
        this.name = name;
        this.count = count;
        this.price = price;
    }

    public String getName() { return name; }
    public int getCount() { return count; }
    public int getPrice() { return price; }

    @Override
    public String toString() {
        return name + " - Count: " + count + " - Price: " + price;
    }
}