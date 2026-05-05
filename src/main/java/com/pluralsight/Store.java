
package com.pluralsight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Store {

    private static final String FILE_NAME = "products.csv";
    // Create lists for inventory and the shopping cart
    private static final HashMap<String, Product> inventoryMap = new HashMap<>();
    private static final ArrayList<Product> inventory = new ArrayList<>();
    private static final ArrayList<Product> cart = new ArrayList<>();

    public static void main(String[] args) {
        // Load inventory from the data file (pipe-delimited: id|name|price)
        loadInventory(FILE_NAME, inventory);

        // Main menu loop
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice != 3) {
            System.out.println("\nWelcome to the Online Store!");
            System.out.println("1. Show Products");
            System.out.println("2. Show Cart");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter 1, 2, or 3.");
                scanner.nextLine();                 // discard bad input
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();                     // clear newline

            switch (choice) {
                case 1 -> displayProducts(inventory, scanner);
                case 2 -> displayCart(cart, scanner);
                case 3 -> System.out.println("Thank you for shopping with us!");
                default -> System.out.println("Invalid choice!");
            }
        }
        scanner.close();
    }

    /**
     * Loads product data from file and puts it into inventory ArrayList.
     *
     * @param fileName file used to read data from
     * @param inventory ArrayList
     */
    public static void loadInventory(String fileName, ArrayList<Product> inventory) {
        try {
            File file = new File(fileName);

            if (!file.exists()){
                file.createNewFile();
            }
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = br.readLine()) != null) {
                try {
                    String[] tokens = line.split("\\|");

                    String id = tokens[0];
                    String name = tokens[1];
                    double price = Double.parseDouble(tokens[2]);

                    Product product = new Product(id, name, price);

                    inventory.add(product);

                } catch (NumberFormatException e) {
                    System.err.println("Bad line; " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file");
        }
    }

    /**
     * Displays all products and lets the user add one to the cart.
     * Typing X returns to the main menu.
     */
    public static void displayProducts(ArrayList<Product> inventory, Scanner scanner) {
        printProducts(inventory);

        boolean running = true;
        while (running){
            System.out.println("\nWelcome to the Product Page");
            System.out.println("A. Add Product to Cart / Search by ID");
            System.out.println("X. Back to Home Screen");

            String choice = scanner.nextLine();

            switch (choice.toUpperCase()) {
                case "A" -> {
                    System.out.println("Enter Item ID");
                    String id = scanner.nextLine().trim();
                    Product product = findProductById(id, inventory);
                    if(product == null){
                        System.out.println("\nProduct not found");
                    }else{
                        System.out.println(product);
                        addToCart(scanner, product);
                    }
                }
                case "X" -> running = false;
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    public static void addToCart(Scanner scanner, Product product) {
        boolean running = true;
        while(running) {
        System.out.println("Add Item to Cart? (Y/N)");
        String command = scanner.nextLine().toUpperCase();
            switch(command) {
                case "N" -> {
                    System.out.println("Item Not Added");
                    running = false;
                }
                case "Y" -> {
                    cart.add(product);
                    System.out.println("Item Added to Cart");
                    running = false;
                }
                default -> System.out.println("Invalid Choice");
            }
        }
    }

    public static void removeFromCart(Scanner scanner) {
        System.out.print("What item would you like to remove: ");
        String id = scanner.nextLine();

        Product product = findProductById(id, cart);

        if (product != null) {
            cart.remove(product);
            System.out.println(product.getName() + " --Was removed from cart.");
        }else {
            System.out.println("Product not found.");
        }
    }

    /**
     * Shows the contents of the cart, calculates the total,
     * and offers the option to check out.
     */
    public static void displayCart(ArrayList<Product> cart, Scanner scanner) {

        System.out.print("\n========== SHOPPING CART ==========");
        System.out.println("=".repeat(24));
        printProducts(cart); // show all items in cart
        System.out.printf("\nyour cart total is: $%.2f%n", calculateTotal(cart)); // call method to calculate total and then displays it

        boolean running = true;
        while (running){
            System.out.println("\nShopping Cart");
            System.out.println("C. CheckOut");
            System.out.println("R. Remove Item");
            System.out.println("X. Back to Home Screen");

            String choice = scanner.nextLine();

            switch (choice.toUpperCase()) {
                case "C" -> {
                    checkOut(cart, scanner);
                    running = false;
                }
                case "R" -> removeFromCart(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    public static double calculateTotal(ArrayList<Product> cart) {
        double total = 0; // set to 0 in case cart is empty
        for (Product product : cart){ // loops through cart
            total += product.getPrice(); // adds total of items inside cart
        }
        return total; // returns total so it can be used by other methods
    }

    public static double enoughMoney(Scanner scanner, double total) {
        double amountGiven = 0;
        while (amountGiven < total) { // loops until amount given is enough to pay for products
            System.out.print("\nPlease enter amount of cash you are giving: $");
            amountGiven = Double.parseDouble(scanner.nextLine());

            if (amountGiven < total) {
                System.out.printf("\nInsufficient funds. You need atleast: $%.2f%n", total);
            }
        }
        return amountGiven;
    }

    public static double calculateChange(double amountGiven, double total) {
        double change = amountGiven - total;
        return change;
    }

    public static void printReceipt(ArrayList<Product> cart, double total, double amountGiven, double change) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println("\n========== RECEIPT ==========");
        System.out.println("Date: " + timestamp);
        System.out.println("-----------------------------");
        printProducts(cart);
        System.out.println("-----------------------------");
        System.out.printf("Total:        $%.2f%n", total);
        System.out.printf("Amount Paid:  $%.2f%n", amountGiven);
        System.out.printf("Change:       $%.2f%n", change);
        System.out.println("=============================");
        System.out.println("Thank you for your purchase!");
    }

    /**
     * Handles the checkout process:
     * 1. Confirm that the user wants to buy.
     * 2. Accept payment and calculate change.
     * 3. Display a simple receipt.
     * 4. Clear the cart.
     */
    public static void checkOut(ArrayList<Product> cart, Scanner scanner) {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        double total = calculateTotal(cart);
        double amountGiven = enoughMoney(scanner, total);
        double change = calculateChange(amountGiven, total);

        printReceipt(cart, total, amountGiven, change);
        cart.clear();
    }

    /**
     * Searches a list for a product by its id.
     *
     * @return the matching Product, or null if not found
     */
    public static Product findProductById(String id, ArrayList<Product> inventory) {
        // TODO: loop over the list and compare ids
        for (Product p : inventory) {
            if (p.getId().equalsIgnoreCase(id)){
                return p;
            }
        }
        return null;
    }

    private static void printProducts(ArrayList<Product> products){
        for (Product p : products) {
            System.out.println(p);
        }
    }
}

 