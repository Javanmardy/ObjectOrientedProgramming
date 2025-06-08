package logic;
import dataaccess.DataAccess;
import model.Item;
import java.util.Collection;

public class BusinessLogic {
    private DataAccess dataAccess;

    public BusinessLogic(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void addItemToList(String name, int count, int price) throws Exception {
        if (count < 1 || price < 0) {
            throw new Exception("Invalid input!");
        }
        dataAccess.addItem(name, count, price);
    }

    public void removeItemFromList(String name) {
        dataAccess.removeItem(name);
    }

    public int calculateTotal() {
        int total = 0;
        for (Item item : dataAccess.getAllItems().values()) {
            total += item.getCount() * item.getPrice();
        }
        return total;
    }

    public Collection<Item> getItems() {
        return dataAccess.getAllItems().values();
    }
}