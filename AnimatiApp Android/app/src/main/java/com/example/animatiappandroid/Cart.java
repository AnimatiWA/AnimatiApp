package com.example.animatiappandroid;

import java.util.ArrayList;

public class Cart {
    private ArrayList<Product> products;

    public Cart() {
        products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(int index) {

        if(index >= 0 && index < products.size()){

            products.remove(index);
        }
    }

    public double getTotalPrice() {
        double total = 0;
        for (Product product : products) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

    public int getCarritoSize(){

        return products.size();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
}