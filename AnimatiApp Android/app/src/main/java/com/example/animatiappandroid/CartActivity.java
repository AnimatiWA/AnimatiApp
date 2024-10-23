package com.example.animatiappandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private Cart cart;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> productNames;
    private RequestQueue queue;
    private TextView totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Inicializo el Volley Request queue.
        queue = Volley.newRequestQueue(this);

        //Inicializo el objeto del carrito y el array para los nombres de los productos.
        cart = new Cart();
        productNames = new ArrayList<>();

        //Recibo idCarrito de sharedPreferences para poder hacer las peticiones.
        SharedPreferences preferences = getSharedPreferences("mi_app_prefs", Context.MODE_PRIVATE);
        int idCarrito = preferences.getInt("idCarrito", -1);
        String token = preferences.getString("token", "");

        //Verifico que el idCarrito sea valido
        if(idCarrito == -1){
            Toast.makeText(this, "Error: carrito inexistente", Toast.LENGTH_LONG).show();
            return;
        }

        ListView productList = findViewById(R.id.product_list);
        Button confirmButton = findViewById(R.id.confirm_button);
        totalPrice = findViewById(R.id.total_price);

        //Cargo los productos del carrito desde el back
        cargarProductosCarrito(idCarrito, token);

        //Configuro el adaptador para mosrar los productos
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productNames);
        productList.setAdapter(adapter);

        // Acción para confirmar la compra
        confirmButton.setOnClickListener(view -> {
            totalPrice.setText("Compra confirmada por $" + cart.getTotalPrice());
        });
    }

    private void cargarProductosCarrito(int idCarrito, String token){

        String url = "https://animatiapp.up.railway.app/carritoProductos/lista/carrito/" + idCarrito;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {

                    try{

                        double total = 0.0;

                        //Limpio la lista para evitar datos repetidos
                        productNames.clear();

                        //Itero sobre el array de productos
                        for(int i = 0; i < response.length(); i++){

                            JSONObject productoCarrito = response.getJSONObject(i);

                            String nombreProducto = productoCarrito.getString("Codigo");
                            double precio = productoCarrito.getDouble("Precio");
                            int cantidad = productoCarrito.getInt("Cantidad");

                            //Agrego el producto al carrito
                            cart.addProduct(new Product(nombreProducto, precio, cantidad));

                            //Actualizo la lista de nombres de productos
                            String elementoProducto = nombreProducto + " - $" + precio + " x " + cantidad;
                            productNames.add(elementoProducto);

                            //Sumo el precio al total
                            total += precio;
                        }

                        //Actualizo el adaptador para que se vean los productos en el ListView
                        adapter.notifyDataSetChanged();

                        //Muestro el precio total en el textView
                        totalPrice.setText("Total: $" + total);

                    } catch (JSONException e){

                        e.printStackTrace();
                    }
                },
                error -> {

                    Toast.makeText(CartActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders(){

                Map<String, String> headers = new HashMap<>();

               //Agrego el token para la autorizacion del endpoint
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        //Agrego la solicitud a la cola de Volley
        queue.add(request);
    }
}