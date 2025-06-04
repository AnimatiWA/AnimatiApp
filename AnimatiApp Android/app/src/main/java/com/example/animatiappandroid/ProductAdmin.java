package com.example.animatiappandroid;

public class ProductAdmin {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int stock;
    private int Id_Categoria;
    private String imagen;

    public ProductAdmin(int id, String name, double price, int quantity, int Id_Categoria, int stock, String imagen) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
        this.Id_Categoria = Id_Categoria;
        this.imagen = imagen;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getStock() {
        return stock;
    }

    public int getCategoria() {
        return Id_Categoria;
    }

    public String getImagen() {
        // Si la imagen ya es una URL completa, la devuelve tal cual, sino añade el prefijo
        return imagen != null && imagen.startsWith("http") ? imagen : "https://animatiapp.up.railway.app/" + imagen;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setId_Categoria(int id_Categoria) {
        this.Id_Categoria = id_Categoria;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
