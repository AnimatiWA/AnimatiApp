package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class MenuAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);

        Button btnGestionar = findViewById(R.id.btnGestionarProductos);
        Button btnResumen = findViewById(R.id.btnResumenVentas);
        Button btnVolverAtras = findViewById(R.id.btnVolverAtras);

        btnGestionar.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAdminActivity.this, AdminActivity.class);
            startActivity(intent);
        });

        btnResumen.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAdminActivity.this, ResumenVentasActivity.class);
            startActivity(intent);
        });
        
        btnVolverAtras.setOnClickListener(v -> {
            finish();
        });
    }
}
