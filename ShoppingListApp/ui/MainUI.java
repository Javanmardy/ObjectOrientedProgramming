package ui;
import db.Database;
import dataaccess.DataAccess;
import logic.BusinessLogic;
import controller.Controller;
import model.Item;
import java.util.Scanner;

public class MainUI {
    public static void main(String[] args) {
        Database db = new Database();
        DataAccess dataAccess = new DataAccess(db);
        BusinessLogic logic = new BusinessLogic(dataAccess);
        Controller controller = new Controller(logic);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add item\n2. Remove item\n3. Show items\n4. Show total\n5. Exit");
            System.out.print("Select option: ");
            String choice = sc.nextLine();
            try {
                if (choice.equals("1")) {
                    System.out.print("Item name: ");
                    String name = sc.nextLine();
                    System.out.print("Count: ");
                    int count = Integer.parseInt(sc.nextLine());
                    System.out.print("Price: ");
                    int price = Integer.parseInt(sc.nextLine());
                    controller.add(name, count, price);
                } else if (choice.equals("2")) {
                    System.out.print("Item name to remove: ");
                    String name = sc.nextLine();
                    controller.remove(name);
                } else if (choice.equals("3")) {
                    System.out.println("Items:");
                    for (Item item : controller.showItems()) {
                        System.out.println(item);
                    }
                } else if (choice.equals("4")) {
                    System.out.println("Total price: " + controller.showTotal());
                } else if (choice.equals("5")) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        sc.close();
    }
}