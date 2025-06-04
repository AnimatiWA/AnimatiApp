package com.example.animatiappandroid;

import java.io.Serializable;

public class PurchaseItem implements Serializable {
    private String date;
    private int quantity;
    private double price;

    public PurchaseItem(String date, int quantity, double price) {
        this.date = date;
        this.quantity = quantity;
        this.price = price;
    }


    public String getDate() {
        return date;
    }

    public int getQuantity() { return quantity; }

    public double getPrice() { return price; }

}

