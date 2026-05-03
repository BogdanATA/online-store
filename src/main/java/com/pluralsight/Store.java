
package com.pluralsight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
     * Loads product data from file and puts it into inventory HashMap.
     *
     * @param fileName file used to read data from
     * @param inventory HashMap
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
    public static void displayProducts(ArrayList<Product> inventory,
                                       Scanner scanner) {
        // TODO: show each product (id, name, price),
        //       prompt for an id, find that product, add to cart
        printProducts(inventory);

        boolean running = true;
        while (running){
            System.out.println("\nWelcome to the Product Page");
            System.out.println("A. Add Product");
            System.out.println("S. Search Product ID");
            System.out.println("X. Back to Home Screen");

            String choice = scanner.nextLine();

            switch (choice.toUpperCase()) {
                case "A" -> System.out.println("ITEM ADDED TO CART"); //addToCart;
                case "S" -> System.out.println("PRODUCT ID SEARCH"); //add search by id function
                case "X" -> running = false;
                default -> System.out.println("Invalid choice!");
            }
        }

    }

    /**
     * Shows the contents of the cart, calculates the total,
     * and offers the option to check out.
     */
    public static void displayCart(ArrayList<Product> cart, Scanner scanner) {
        // TODO:
        //   • list each product in the cart
        //   • compute the total cost
        //   • ask the user whether to check out (C) or return (X)
        //   • if C, call checkOut(cart, totalAmount, scanner)
        printProducts(cart);

        boolean running = true;
        while (running){
            System.out.println("\nShopping Cart");
            System.out.println("C. CheckOut");
            System.out.println("X. Back to Home Screen");

            String choice = scanner.nextLine();

            switch (choice.toUpperCase()) {
                case "C" -> running = true; //addToCart;
                case "X" -> running = false;
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    /**
     * Handles the checkout process:
     * 1. Confirm that the user wants to buy.
     * 2. Accept payment and calculate change.
     * 3. Display a simple receipt.
     * 4. Clear the cart.
     */
    public static void checkOut(ArrayList<Product> cart,
                                double totalAmount,
                                Scanner scanner) {
        // TODO: implement steps listed above
    }

    /**
     * Searches a list for a product by its id.
     *
     * @return the matching Product, or null if not found
     */
    public static Product findProductById(String id, ArrayList<Product> inventory) {
        // TODO: loop over the list and compare ids
        return null;
    }

    private static void printProducts(ArrayList<Product> products){ //Collection is parent of hashmap and array list so it accepts both
        for (Product p : products) {
            System.out.println(p);
        }
    }
}

 