package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CuotasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuotas);


        TextView cuota1 = findViewById(R.id.textViewCuota1);
        TextView cuota2 = findViewById(R.id.textViewCuota2);
        TextView cuota3 = findViewById(R.id.textViewCuota3);
        TextView cuota4 = findViewById(R.id.textViewCuota4);
        TextView cuota5 = findViewById(R.id.textViewCuota5);
        TextView cuota6 = findViewById(R.id.textViewCuota6);
        TextView cuota7 = findViewById(R.id.textViewCuota7);
        TextView cuota8 = findViewById(R.id.textViewCuota8);
        TextView cuota9 = findViewById(R.id.textViewCuota9);
        TextView cuota10 = findViewById(R.id.textViewCuota10);
        TextView cuota11 = findViewById(R.id.textViewCuota11);
        TextView cuota12 = findViewById(R.id.textViewCuota12);

        View.OnClickListener navigateListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para ir a NumerosTarjetaActivity
                Intent intent = new Intent(CuotasActivity.this, NumerosTarjetaActivity.class);
                startActivity(intent);
            }
        };


        cuota1.setOnClickListener(navigateListener);
        cuota2.setOnClickListener(navigateListener);
        cuota3.setOnClickListener(navigateListener);
        cuota4.setOnClickListener(navigateListener);
        cuota5.setOnClickListener(navigateListener);
        cuota6.setOnClickListener(navigateListener);
        cuota7.setOnClickListener(navigateListener);
        cuota8.setOnClickListener(navigateListener);
        cuota9.setOnClickListener(navigateListener);
        cuota10.setOnClickListener(navigateListener);
        cuota11.setOnClickListener(navigateListener);
        cuota12.setOnClickListener(navigateListener);
    }

    public void navigateToNumerosTarjeta(View view) {
        Intent intent = new Intent(this, NumerosTarjetaActivity.class);
        startActivity(intent);
    }
}
