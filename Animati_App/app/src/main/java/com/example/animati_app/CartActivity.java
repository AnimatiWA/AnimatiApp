package com.example.animati_app;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ListView cartListView;
    private ArrayList<CartItem> cartItems;
    private CartAdapter cartAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new DatabaseHelper(this);
        cartListView = findViewById(R.id.cartListView);
        cartItems = dbHelper.getCartItems();

        cartAdapter = new CartAdapter(this, cartItems);
        cartListView.setAdapter(cartAdapter);
    }
}
