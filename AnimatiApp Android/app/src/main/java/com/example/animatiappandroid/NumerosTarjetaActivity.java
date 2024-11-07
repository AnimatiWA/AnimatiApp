package com.example.animatiappandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NumerosTarjetaActivity extends AppCompatActivity {

    private EditText editTextCardNumber, editTextCardExpiration, editTextSecurityCode;
    private ImageView imageViewCardType;
    private Button confirmButton;
    private RequestQueue queue;
    private SharedPreferences preferences;
    private double total;
    private int idCarrito;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeros_tarjeta);

        editTextCardNumber = findViewById(R.id.editTextCardNumber);
        editTextCardExpiration = findViewById(R.id.editTextExpirationDate);
        editTextSecurityCode = findViewById(R.id.editTextSecurityCode);
        imageViewCardType = findViewById(R.id.imageViewCardType);
        confirmButton = findViewById(R.id.buttonSubmit);

        queue = Volley.newRequestQueue(this);
        preferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);
        idCarrito = preferences.getInt("idCarrito", -1);
        token = preferences.getString("token", "");

        // Aquí sería ideal cargar el total del carrito desde el backend si no se tiene ya el valor.
        total = preferences.getFloat("totalCarrito", 0.0f);

        if (idCarrito == -1) {
            Toast.makeText(this, "Carrito vacío o no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        confirmButton.setOnClickListener(this::confirmarCompra);
    }

    private void confirmarCompra(View view) {
        if (total <= 0.0) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_LONG).show();
            return;
        }

        String url = "https://animatiapp.up.railway.app/api/carrito/crear";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                response -> {
                    try {
                        int nuevoIdCarrito = response.getInt("id");

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("idCarrito", nuevoIdCarrito);
                        editor.apply();

                        Toast.makeText(NumerosTarjetaActivity.this, "Compra confirmada", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(NumerosTarjetaActivity.this, activity_inicio.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(NumerosTarjetaActivity.this, "Error al confirmar la compra", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String mensajeError = "Error al confirmar la compra";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String errorData = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorObject = new JSONObject(errorData);

                            if (errorObject.has("error")) {
                                mensajeError = errorObject.getString("error");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Toast.makeText(NumerosTarjetaActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}
