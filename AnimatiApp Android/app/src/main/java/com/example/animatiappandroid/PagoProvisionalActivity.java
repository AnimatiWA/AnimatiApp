package com.example.animatiappandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PagoProvisionalActivity extends AppCompatActivity {

    private TextView totalPrice;
    private RequestQueue queue;
    private int idCarrito;
    private String token;
    private double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_provisional);

        totalPrice = findViewById(R.id.total_price);
        queue = Volley.newRequestQueue(this);

        SharedPreferences preferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);
        idCarrito = preferences.getInt("idCarrito", -1);
        token = preferences.getString("token", "");

        if (idCarrito == -1) {
            totalPrice.setText("No hay carrito activo");
            return;
        }

        obtenerTotalCarrito();
    }

    private void obtenerTotalCarrito() {
        String url = "https://animatiapp.up.railway.app/api/carritoProductos/lista/carrito/" + idCarrito;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        total = 0.0;
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject producto = response.getJSONObject(i);
                            double precio = producto.getDouble("Precio");
                            total += precio;
                        }
                        totalPrice.setText("Total: $" + total);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        totalPrice.setText("Error al cargar total");
                    }
                },
                error -> {
                    totalPrice.setText("Error de conexión");
                    Toast.makeText(PagoProvisionalActivity.this, "No se pudo obtener el total", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }
}

