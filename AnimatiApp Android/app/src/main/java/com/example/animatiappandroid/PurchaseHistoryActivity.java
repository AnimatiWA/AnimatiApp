package com.example.animatiappandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PurchaseAdapter purchaseAdapter;
    private List<PurchaseItem> purchaseList;
    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Simulamos datos
        purchaseList = new ArrayList<>();
        purchaseList.add(new PurchaseItem("2024-05-23",6, 1500));
        purchaseList.add(new PurchaseItem("2024-05-15",5,  2500));
        purchaseList.add(new PurchaseItem("2024-05-02",4,  1100));

        requestQueue = Volley.newRequestQueue(this);

        sharedPreferences = getSharedPreferences("AnimatiPreferencias", MODE_PRIVATE);

        token = sharedPreferences.getString("token", "");

        cargarCompras();

        Button bottonAtras = findViewById(R.id.back_button);
        bottonAtras.setOnClickListener(view -> {
            finish();
        });
    }

    private void cargarCompras(){

        purchaseList = new ArrayList<>();

        String url = "https://animatiapp.up.railway.app/api/carrito/historial";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try{

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject purchaseObject = response.getJSONObject(i);
                                String fecha = purchaseObject.getString("Fecha");
                                int cantidad = purchaseObject.getInt("Cantidad");
                                double precio = purchaseObject.getDouble("Precio");

                                purchaseList.add(new PurchaseItem(fecha, cantidad, precio));
                            }

                            Collections.reverse(purchaseList);

                            purchaseAdapter = new PurchaseAdapter(PurchaseHistoryActivity.this, purchaseList);
                            recyclerView.setAdapter(purchaseAdapter);
                            purchaseAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {

                            e.printStackTrace();
                            Toast.makeText(PurchaseHistoryActivity.this, "Error al procesar las compras", Toast.LENGTH_SHORT);
                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(PurchaseHistoryActivity.this, "Error en la petición: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("PurchaseHistoryActivity", "Cargar compras fallo");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }
}


