package com.example.animatiappandroid;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int stock;
    private int Id_Categoria;
    private String imagen;

    public Product(int id, String name, double price, int quantity, int Id_Categoria, int stock, String imagen) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
        this.imagen = imagen;
        this.Id_Categoria = Id_Categoria;
    }

    // Getters
    public int getId() { return id; }

    public String getName() { return name; }

    public double getPrice() { return price; }

    public int getQuantity() { return quantity; }

    public int getStock() { return stock; }


    public int getCategoria() {
        return Id_Categoria;
    }


    public String getImagen() {
        return imagen.startsWith("http") ? imagen : "https://animatiapp.up.railway.app/" + imagen;
    }

    // Setters
    public void setName(String name) { this.name = name; }

    public void setPrice(double price) { this.price = price; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setStock(int stock) { this.stock = stock; }

    public void setId_Categoria(int id_Categoria) { this.Id_Categoria = id_Categoria; }

    public void setImagen(String imagen) { this.imagen = imagen; }
}
