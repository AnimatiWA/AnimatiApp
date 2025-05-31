package com.example.animatiappandroid;

public class PurchaseItem {
    private String date;
    private int quantity;
    private int price;

    public PurchaseItem(String date, int quantity, int price) {
        this.date = date;
        this.quantity = quantity;
        this.price = price;
    }


    public String getDate() {
        return date;
    }

    public int getPrice() { return price; }

    public int getQuantity() { return quantity; }

}

