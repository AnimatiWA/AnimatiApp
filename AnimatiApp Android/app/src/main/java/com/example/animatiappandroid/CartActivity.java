package com.example.animatiappandroid;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private Cart cart;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> productNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cart = new Cart();
        productNames = new ArrayList<>();

        // Agregar productos de ejemplo
        cart.addProduct(new Product("Laptop", 1000.00, 1));
        cart.addProduct(new Product("Mouse", 25.00, 2));

        ListView productList = findViewById(R.id.product_list);
        TextView totalPrice = findViewById(R.id.total_price);
        Button confirmButton = findViewById(R.id.confirm_button);

        // Mostrar productos en el ListView
        for (Product product : cart.getProducts()) {
            productNames.add(product.getName() + " - $" + product.getPrice() + " x" + product.getQuantity());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productNames);
        productList.setAdapter(adapter);

        // Actualizar el precio total
        totalPrice.setText("Total: $" + cart.getTotalPrice());

        // Acción para confirmar la compra
        confirmButton.setOnClickListener(view -> {
            totalPrice.setText("Compra confirmada por $" + cart.getTotalPrice());
        });
    }
}