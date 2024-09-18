package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    private Button btnInicio, btnContacto, btnSobreNosotros, btnCart, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Inicializar los botones
        btnInicio = findViewById(R.id.btn_inicio);
        btnContacto = findViewById(R.id.btn_contacto);
        btnSobreNosotros = findViewById(R.id.btn_sobre_nosotros);
        btnCart = findViewById(R.id.btn_cart);
        btnRegister = findViewById(R.id.btn_register);

        // Abrir actividad de Inicio
        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, InicioActivity.class);
                startActivity(intent);
            }
        });

        // Abrir actividad de Contacto
        btnContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, ContactoActivity.class);
                startActivity(intent);
            }
        });

        // Abrir actividad de Sobre Nosotros
        btnSobreNosotros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, SobreNosotrosActivity.class);
                startActivity(intent);
            }
        });

        // Abrir actividad de Carrito
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        // Abrir actividad de Registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
