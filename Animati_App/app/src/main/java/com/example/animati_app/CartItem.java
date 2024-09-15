// File: app/src/main/java/com/example/animati_app/CartItem.java
package com.example.animati_app;

public class CartItem {
    private int id;
    private String productName;
    private int quantity;
    private double totalPrice;

    public CartItem(int id, String productName, int quantity, double totalPrice) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
