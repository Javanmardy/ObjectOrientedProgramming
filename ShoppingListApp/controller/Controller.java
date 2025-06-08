package controller;
import logic.BusinessLogic;
import model.Item;
import java.util.Collection;

public class Controller {
    private BusinessLogic logic;

    public Controller(BusinessLogic logic) {
        this.logic = logic;
    }

    public void add(String name, int count, int price) throws Exception {
        logic.addItemToList(name, count, price);
    }

    public void remove(String name) {
        logic.removeItemFromList(name);
    }

    public int showTotal() {
        return logic.calculateTotal();
    }

    public Collection<Item> showItems() {
        return logic.getItems();
    }
}