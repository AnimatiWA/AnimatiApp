package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.animatiappandroid.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_gallery);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList=new ArrayList<>();
        productList.add(new Product("Product 1", 10.99, 1));
        productList.add(new Product("Product 2", 19.99, 1));
        productList.add(new Product("Product 3", 29.99, 1));

        productAdapter=new ProductAdapter(productList,this);
        recyclerView.setAdapter(productAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void op_carrito(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    public void op_menu(View view) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    public void viewCart(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }


}
