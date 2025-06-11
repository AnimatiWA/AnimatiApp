package com.example.animatiappandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
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

public class PurchaseRegretActivity extends AppCompatActivity {

    TextView mensaje;
    Button btnCancelar, btnAceptar;
    PurchaseItem purchaseItem;
    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_regret);

        purchaseItem = (PurchaseItem) getIntent().getSerializableExtra("purchaseItem");

        mensaje = findViewById(R.id.mensaje);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnAceptar = findViewById(R.id.btnAceptar);

        requestQueue = Volley.newRequestQueue(this);

        sharedPreferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);

        token = sharedPreferences.getString("token", "");

        mensaje.setText("¿Está seguro de que quiere reembolsar la compra de " + purchaseItem.getQuantity() + " artículos por el monto de " + purchaseItem.getPrice() + "$ ?");

        btnCancelar.setOnClickListener(v -> cancelarArrepentimiento());

        btnAceptar.setOnClickListener(v -> aceptarArrepentimiento());
    }

    private void cancelarArrepentimiento(){

        Toast.makeText(this, "Devolución cancelada", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void aceptarArrepentimiento(){

        btnAceptar.setEnabled(false);
        btnCancelar.setEnabled(false);

        String url = "https://animatiapp.up.railway.app/api/carrito/eliminar/" + purchaseItem.getId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(PurchaseRegretActivity.this, "Devolución en proceso. Nuestro equipo se pondra en contacto pronto.", Toast.LENGTH_SHORT).show();

                        Intent result = new Intent();
                        result.putExtra("refresh", true);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PurchaseRegretActivity.this, "Error en la petición: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("PurchaseRegretActivity","Error al borrar carrito");
                btnAceptar.setEnabled(true);
                btnCancelar.setEnabled(true);
            }
        }

        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}