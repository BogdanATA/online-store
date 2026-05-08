
package com.pluralsight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;


public class Store {

    private static final String FILE_NAME = "products.csv";
    // Create lists for inventory and the shopping cart
    private static final ArrayList<Product> inventory = new ArrayList<>();
    private static final ArrayList<CartItem> cart = new ArrayList<>();

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
                scanner.nextLine();
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();

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
     * Loads product data from file and puts it into inventory ArrayList
     *
     * @param fileName file used to read data from
     * @param inventory ArrayList that store the products from file
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
                String[] tokens = line.split("\\|");

                if (tokens.length != 3) continue; // if line inside file doesnt have exactly 3 tokens skip it

                try {
                    String id = tokens[0];
                    String name = tokens[1];
                    double price = Double.parseDouble(tokens[2]);

                    Product product = new Product(id, name, price);

                    inventory.add(product);

                } catch (NumberFormatException e) { // catches error if file has bad lines (ex. price has a letter in it)
                    System.err.println("Bad line; " + line);
                }
            }
        } catch (IOException e) { // catches errors if we are not able to access the file
            System.err.println("Error reading file");
        }
    }

    /**
     * Displays all products, lets user search by id and add to cart
     *
     * @param inventory list of all available products
     * @param scanner used to read user input
     */
    public static void displayProducts(ArrayList<Product> inventory, Scanner scanner) {
        boolean running = true;
        while (running){
            printProducts(inventory);

            System.out.println("\nWelcome to the Product Page");
            System.out.println("A. Add Product to Cart / Search by ID");
            System.out.println("X. Back to Home Screen");

            String choice = scanner.nextLine();

            switch (choice.toUpperCase()) {
                case "A" -> {
                    System.out.println("Enter Item ID");
                    String id = scanner.nextLine().trim();
                    Product product = findProductById(id, inventory); // search inventory for product matching user given id
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

    /**
     * Adds items to cart
     *
     * @param scanner used to read user input
     * @param product the product getting added to cart
     * */
    public static void addToCart(Scanner scanner, Product product) {
        boolean running = true;
        while(running) {
        System.out.println("\nAdd Item to Cart? (Y/N)");
        String command = scanner.nextLine().toUpperCase();
            switch(command) {
                case "N" -> {
                    System.out.println("Item Not Added");
                    running = false;
                }
                case "Y" -> {
                    // if cart items exists increment its quantity
                    boolean found = false;
                    for (CartItem item : cart) {
                        if (item.getId().equalsIgnoreCase(product.getId())) { // compares id of CartItem to the id of the item the user is adding
                            item.increaseQuantity();  // if item with matching id exists increment the quantity
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        cart.add(new CartItem(product, 1)); // if cart item does not exist add the product with a quantity of 1
                    }
                    System.out.println("Item Added to Cart");
                    running = false;
                }
                default -> System.out.println("Invalid Choice");
            }
        }
    }

    /**
     * Removes item from cart
     *
     * @param scanner used to read user input
     * */
    public static void removeFromCart(Scanner scanner) {
        System.out.print("What item would you like to remove: ");
        String id = scanner.nextLine().trim();

        boolean found = false;
        for (CartItem item : cart) {
            if (item.getId().equalsIgnoreCase(id)) {
                if (item.getQuantity() > 1) {
                    item.decreaseQuantity();  // if item with matching id exists decrease the quantity
                    System.out.println("Item removed");
                }else {
                    cart.remove(item); // if item is found but its quantity is 1 it removes it
                    System.out.println("Item removed");
                }
            found = true;
            break;
            }
        }
        if (!found) System.out.println("Product not found.");
    }

    /**
     * Displays all items in cart and lets user checkout
     *
     * @param cart list of all items in cart that get printed
     * @param scanner used to read user input
     */
    public static void displayCart(ArrayList<CartItem> cart, Scanner scanner) {
        boolean running = true;
        while (running){
            System.out.print("\n========== SHOPPING CART ==========");
            System.out.println("=".repeat(30));
            printCart(cart); // show all items in cart
            System.out.printf("\nyour cart total is: $%.2f%n", calculateTotal(cart)); // call method to calculate total and then displays it

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

    /**
     * Calculates total of items in cart
     *
     * @param cart list of items whos price gets calculated
     * @return total cost of items in cart
     * */
    public static double calculateTotal(ArrayList<CartItem> cart) {
        double total = 0; // set to 0 in case cart is empty
        for (CartItem item : cart){ // loops through cart
            total += item.getSubtotal(); // adds total of items inside cart
        }
        return total; // returns total so it can be used by other methods
    }

    /**
     * Checks to see if user gave enough money for the transaction
     *
     * @param scanner used to read user input
     * @param total amount of money required to pay
     * @return amount of money the user gave
     * */
    public static double enoughMoney(Scanner scanner, double total) {
        double amountGiven = 0;
        while (amountGiven < total) { // loops until amount given is enough to pay for products
            System.out.print("\nPlease enter amount of cash you are giving: $");
            try {
                amountGiven = Double.parseDouble(scanner.nextLine());

                if (amountGiven < total) {
                    System.out.printf("\nInsufficient funds. You need atleast: $%.2f%n", total);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return amountGiven;
    }

    /**
     * Calculates the change owed to user
     *
     * @param amountGiven Amount of money given by user
     * @param total Total cost of cart
     * @return Amount of change owed to user
     * */
    public static double calculateChange(double amountGiven, double total) {
        double change = amountGiven - total;
        return change;
    }

    /**
     * Prints receipt from transaction
     *
     * @param cart List of items purchased
     * @param total Total cost of items in cart
     * @param amountGiven Amount of money given by user
     * @param change Amount of change owed to user
     * */
    public static void printReceipt(ArrayList<CartItem> cart, double total, double amountGiven, double change) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println("\n========== RECEIPT ==========");
        System.out.println("Date: " + timestamp);
        System.out.println("-----------------------------");
        printCart(cart);
        System.out.println("-----------------------------");
        System.out.printf("Total:        $%.2f%n", total);
        System.out.printf("Amount Paid:  $%.2f%n", amountGiven);
        System.out.printf("Change:       $%.2f%n", change);
        System.out.println("=============================");
        System.out.println("Thank you for your purchase!");
    }

    /**
     * Handles the checkout flow and clears cart after purchase
     *
     * @param cart List of items to purchase
     * @param scanner Used to read user input
     */
    public static void checkOut(ArrayList<CartItem> cart, Scanner scanner) {
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
     * Finds products with matching id's
     *
     * @param id The id to search for
     * @param inventory The list to search through
     * @return The matching product, or null if not found
     */
    public static Product findProductById(String id, ArrayList<Product> inventory) {
        for (Product p : inventory) {
            if (p.getId().equalsIgnoreCase(id)){
                return p;
            }
        }
        return null;
    }

    /**
     * Prints all products in the list
     *
     * @param products List of products to print
     * */
    private static void printProducts(ArrayList<Product> products){
        for (Product p : products) {
            System.out.println(p);
        }
    }

    /**
     * Prints all products in the cart list
     *
     * @param cart List of cart items to print
     * */
    private static void printCart(ArrayList<CartItem> cart) {
        for (CartItem item : cart) {
            System.out.println(item);
        }
    }
}

 