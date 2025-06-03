package com.example.animatiappandroid;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int stock;

    public Product(int id, String name, double price, int quantity, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
    }

    public int getStock() { return stock; }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {

        this.price = price;
    }

    public int getId() { return id; }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}