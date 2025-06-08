package db;
import java.util.*;
import model.Item;

public class Database {
    private Map<String, Item> items = new HashMap<>();

    public Map<String, Item> getItems() {
        return items;
    }

    public void setItem(String name, int count, int price) {
        items.put(name, new Item(name, count, price));
    }

    public void deleteItem(String name) {
        items.remove(name);
    }
}