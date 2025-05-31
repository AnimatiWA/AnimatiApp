package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PurchaseAdapter adapter;
    private List<PurchaseItem> purchaseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Simulamos datos
        purchaseList = new ArrayList<>();
        purchaseList.add(new PurchaseItem("2024-05-23",6, 1500));
        purchaseList.add(new PurchaseItem("2024-05-15",5,  2500));
        purchaseList.add(new PurchaseItem("2024-05-02",4,  1100));

        adapter = new PurchaseAdapter(purchaseList);
        recyclerView.setAdapter(adapter);

        Button bottonAtras = findViewById(R.id.back_button);
        bottonAtras.setOnClickListener(view -> {
            finish();
        });
    }
}


