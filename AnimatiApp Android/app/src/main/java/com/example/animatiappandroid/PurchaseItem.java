package com.example.animatiappandroid;

import java.io.Serializable;

public class PurchaseItem implements Serializable {
    private int id;
    private String date;
    private int quantity;
    private double price;
    private String confirmado;

    public PurchaseItem(int id, String date, int quantity, double price, String confirmado) {
        this.id = id;
        this.date = date;
        this.quantity = quantity;
        this.price = price;
        this.confirmado = confirmado;
    }


    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getQuantity() { return quantity; }

    public double getPrice() { return price; }

    public String getConfirmado() { return confirmado; }
}