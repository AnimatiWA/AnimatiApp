package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.animatiappandroid.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gallery extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_gallery);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        requestQueue = Volley.newRequestQueue(this);

        sharedPreferences=getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);

        token=sharedPreferences.getString("token","");

        cargarProductos();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void op_carrito(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    public void op_menu(View view) {
        Intent intent = new Intent(this, activity_inicio.class);
        startActivity(intent);
    }

    public void cargarProductos(){

        productList = new ArrayList<>();

        String url = "https://animatiapp.up.railway.app/api/producto/lista";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject productObject = response.getJSONObject(i);
                                int id = productObject.getInt("Codigo_Producto");
                                String nombre = productObject.getString("Nombre_Producto");
                                double precio = productObject.getDouble("Precio");
                                int stock = productObject.getInt("Stock");
                                int cantidad = 1;

                                productList.add(new Product(id, nombre, precio, cantidad, stock));
                            }

                            productAdapter = new ProductAdapter(productList, Gallery.this);
                            recyclerView.setAdapter(productAdapter);
                            productAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Gallery.this, "Error procesando los datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Gallery.this, "Error en la petición: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Gallery","Cargar producto fallo");
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
        requestQueue.add(jsonArrayRequest);
    }

    public void agregarAlCarrito(Product product) {

        int idCarrito = sharedPreferences.getInt("idCarrito", -1);

        if (idCarrito == -1) {
            crearYAgregar(product);
        }
        else {
            agregar(product);
        }
    }

    public void crearYAgregar(Product product) {
        String url = "https://animatiapp.up.railway.app/api/carrito/crear";

        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int idCarrito = response.getInt("id");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("idCarrito", idCarrito);
                            editor.apply();
                            agregar(product);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Gallery.this, "Error en la petición: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Gallery","Cargar y agregar fallo");
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void agregar(Product product) {
        int idCarrito = sharedPreferences.getInt("idCarrito", -1);
        int codigoProducto = product.getId();
        int cantidad = product.getQuantity();


        if (idCarrito == -1) {
            Toast.makeText(this, "Carrito no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://animatiapp.up.railway.app/api/carritoProductos/crear";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Codigo", codigoProducto);
            jsonObject.put("Carrito", idCarrito);
            jsonObject.put("Cantidad", cantidad);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(Gallery.this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String mensajeError = "Error: No se pudo agregar al carrito";

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

                        Toast.makeText(Gallery.this, mensajeError, Toast.LENGTH_SHORT).show();
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
