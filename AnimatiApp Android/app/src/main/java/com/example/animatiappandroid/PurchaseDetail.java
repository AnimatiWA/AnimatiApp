package com.example.animatiappandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import java.util.HashMap;
import java.util.Map;

public class PurchaseDetail extends AppCompatActivity {

    private PurchaseItem purchaseItem;
    private SharedPreferences sharedPreferences;
    private ProductHistoryAdapter productHistoryAdapter;
    private RecyclerView recyclerView;
    private String token;
    private RequestQueue requestQueue;
    private ArrayList<Product> productList;
    private Button botonAtras;
    private TextView dateTextView, totalTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_purchase_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        botonAtras = findViewById(R.id.back_button);

        dateTextView = findViewById(R.id.date_textview);
        totalTextview = findViewById(R.id.total_textview);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<Product>();

        productHistoryAdapter = new ProductHistoryAdapter(productList);
        recyclerView.setAdapter(productHistoryAdapter);

        purchaseItem = (PurchaseItem) getIntent().getSerializableExtra("purchaseItem");

        requestQueue = Volley.newRequestQueue(this);

        sharedPreferences = getSharedPreferences("AnimatiPreferencias", MODE_PRIVATE);

        token = sharedPreferences.getString("token", "");

        dateTextView.setText("Compra del día " + purchaseItem.getDate().replace("-", "/"));

        cargarProductosCarrito();

        botonAtras.setOnClickListener(view -> {
            finish();
        });
    }

    private void cargarProductosCarrito(){

        String url = "https://animatiapp.up.railway.app/api/carritoProductos/lista/carrito/" + purchaseItem.getId();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try{

                            Double total = 0.0;

                            for(int i = 0; i < response.length(); i++){

                                JSONObject productHistoryItem = response.getJSONObject(i);

                                int id = productHistoryItem.getInt("id");
                                String name = productHistoryItem.getString("nombre_producto");
                                double price = productHistoryItem.getDouble("Precio");
                                int quantity = productHistoryItem.getInt("Cantidad");

                                Product product = new Product(id, name, price, quantity, 0);

                                total += price;

                                productList.add(product);
                            }

                            productHistoryAdapter.notifyDataSetChanged();
                            totalTextview.setText("Precio Total: $" + total);

                        } catch (JSONException e){

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(PurchaseDetail.this, "Error en la petición: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("PurchaseHistoryActivity", "Cargar productos de la compra falló");
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