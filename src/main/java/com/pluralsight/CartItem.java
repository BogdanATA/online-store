package com.pluralsight;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity() {
        this.quantity++;
    }

    public void decreaseQuantity() {
        if(quantity > 0) this.quantity--;
    }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    public String getId() {
        return product.getId(); // added so you can get product id from CartItem
    }

    @Override
    public String toString() {
        return String.format("[%s] %-40s x%-3d - $%.2f", product.getId(), product.getName(), quantity, getSubtotal());
    }
}
