package com.example.animatiappandroid;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PurchaseRegretActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_regret);

        PurchaseItem purchaseItem = (PurchaseItem) getIntent().getSerializableExtra("purchaseItem");

        Toast.makeText(this, "Compra devuelta: " + purchaseItem.getDate(), Toast.LENGTH_SHORT).show();
    }
}