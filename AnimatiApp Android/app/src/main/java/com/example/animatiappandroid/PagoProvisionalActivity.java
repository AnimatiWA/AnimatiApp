package com.example.animatiappandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PagoProvisionalActivity extends AppCompatActivity {

    private TextView totalPrice, tituloPago, subtitulo, mensajeDemora, textoProcesando;
    private ProgressBar spinner;
    private RequestQueue queue;
    private int idCarrito;
    private String token;
    private double total;

    private Handler handler = new Handler();
    private boolean pagoConfirmado = false;
    private int segundosEsperados = 0;
    private static final int INTERVALO_POLLING = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_provisional);

        // Referencias a las vistas
        tituloPago = findViewById(R.id.titulo_pago);
        subtitulo = findViewById(R.id.subtitulo_pago);
        mensajeDemora = findViewById(R.id.mensaje_demora);
        spinner = findViewById(R.id.progress_bar_pago);
        textoProcesando = findViewById(R.id.texto_procesando_pago);
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
        comenzarPollingEstadoPago();
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

    private void comenzarPollingEstadoPago() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                segundosEsperados += 3;

                consultarEstadoPagoDesdeAPI();

                if (!pagoConfirmado) {
                    handler.postDelayed(this, INTERVALO_POLLING);
                }
            }
        }, INTERVALO_POLLING);
    }

    private void consultarEstadoPagoDesdeAPI() {
        String url = "https://animatiapp.up.railway.app/api/carrito/" + idCarrito;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String estado = response.getString("estado");

                        if (estado.equalsIgnoreCase("aprobado")) {
                            mostrarCompraExitosa();
                        } else if (segundosEsperados >= 15) {
                            mensajeDemora.setVisibility(View.VISIBLE);
                        }
                        // Si es "pendiente", seguimos esperando
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(PagoProvisionalActivity.this, "Error al verificar estado del pago", Toast.LENGTH_SHORT).show();
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

    private void mostrarCompraExitosa() {
        pagoConfirmado = true;
        spinner.setVisibility(View.GONE);
        textoProcesando.setVisibility(View.GONE);
        mensajeDemora.setVisibility(View.GONE);
        subtitulo.setVisibility(View.GONE);

        tituloPago.setText("✅ ¡Tu pago fue aprobado exitosamente!");

        // Espera 2 segundos y cambia de pantalla
        handler.postDelayed(() -> {
            Intent intent = new Intent(PagoProvisionalActivity.this, CompraConfirmadaActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}

