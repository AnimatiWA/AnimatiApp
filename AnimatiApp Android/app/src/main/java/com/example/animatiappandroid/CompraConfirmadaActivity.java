package com.example.animatiappandroid;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CompraConfirmadaActivity extends AppCompatActivity {

    private TextView mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra_confirmada);

        mensaje = findViewById(R.id.mensaje_confirmacion);
        mensaje.setText("✅ ¡Tu compra fue realizada con éxito!");

    }
}
