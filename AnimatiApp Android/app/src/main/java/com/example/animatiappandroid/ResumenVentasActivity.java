package com.example.animatiappandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResumenVentasActivity extends AppCompatActivity {

    private String token;

    private TextView tvTotalVentas, tvTotalIngresos, tvProductosVendidos;
    private static final String URL_ENDPOINT = "https://animatiapp.up.railway.app/api/pedidos/resumenCompras";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_ventas);

        // Obtener token del administrador desde SharedPreferences
        SharedPreferences preferences = getSharedPreferences("AnimatiPreferencias", MODE_PRIVATE);
        token = preferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "No tienes permisos para ver este resumen", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tvTotalVentas = findViewById(R.id.tvTotalVentas);
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos);
        tvProductosVendidos = findViewById(R.id.tvProductosVendidos);

        // Cargar los datos del endpoint
        cargarResumenVentasDesdeAPI();
    }

    private void cargarResumenVentasDesdeAPI() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_ENDPOINT,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int totalVentas = response.getInt("total_ventas");
                            double totalIngresos = response.getDouble("total_ingresos");
                            int productosVendidos = response.getInt("productos_vendidos");

                            tvTotalVentas.setText("Total ventas: " + totalVentas);
                            tvTotalIngresos.setText("Total ingresos: $" + totalIngresos);
                            tvProductosVendidos.setText("Productos vendidos: " + productosVendidos);

                        } catch (JSONException e) {
                            Toast.makeText(ResumenVentasActivity.this, "Error parseando los datos", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String mensajeError = "Error en la conexión";
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            mensajeError = "No autorizado. Solo el administrador puede acceder.";
                        }
                        Toast.makeText(ResumenVentasActivity.this, mensajeError, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }
}
