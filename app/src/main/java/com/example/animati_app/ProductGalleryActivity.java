package com.example.animati_app;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ProductGalleryActivity extends AppCompatActivity {

    private ListView productListView;
    private ArrayList<Product> products;
    private ProductAdapter productAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_gallery);

        dbHelper = new DatabaseHelper(this);
        productListView = findViewById(R.id.productListView);
        products = dbHelper.getAllProducts();

        productAdapter = new ProductAdapter(this, products);
        productListView.setAdapter(productAdapter);
    }
}
