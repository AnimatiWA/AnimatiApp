package com.example.animatiappandroid;

public class PurchaseItem {
    private String productName;
    private String date;
    private String price;

    public PurchaseItem(String productName, String date, String price) {
        this.productName = productName;
        this.date = date;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }
}

