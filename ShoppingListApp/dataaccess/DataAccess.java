package dataaccess;

import db.Database;
import model.Item;
import java.util.Map;

public class DataAccess {
    private Database db;

    public DataAccess(Database db) {
        this.db = db;
    }

    public void addItem(String name, int count, int price) {
        db.setItem(name, count, price);
    }

    public void removeItem(String name) {
        db.deleteItem(name);
    }

    public Map<String, Item> getAllItems() {
        return db.getItems();
    }
}