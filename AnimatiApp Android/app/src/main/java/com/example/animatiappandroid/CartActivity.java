package com.example.animatiappandroid;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.animatiappandroid.ProductListAdapter;

import android.view.View;
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

public class CartActivity extends AppCompatActivity implements ProductListAdapter.onRemoveClickListener{
    private Cart cart;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> productNames;
    private RequestQueue queue;
    private TextView totalPrice;
    private int idCarrito;
    private String token;

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
        SharedPreferences preferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);
        idCarrito = preferences.getInt("idCarrito", -1);
        token = preferences.getString("token", "");

        //Verifico que el idCarrito sea valido
        if(idCarrito == -1){
            Toast.makeText(this, "Error: carrito inexistente", Toast.LENGTH_LONG).show();
            return;
        }

        ListView productList = findViewById(R.id.product_list);
        Button confirmButton = findViewById(R.id.confirm_button);
        Button backButton = findViewById(R.id.back_button);
        totalPrice = findViewById(R.id.total_price);

        //Cargo los productos del carrito desde el back
        cargarProductosCarrito();

        //Configuro el adaptador para mosrar los productos
        adapter = new ProductListAdapter(this, productNames, this);
        productList.setAdapter(adapter);

        // Acción para confirmar la compra
        confirmButton.setOnClickListener(this::confirmarCompra);

        backButton.setOnClickListener(this::volverAtras);
    }

    public void volverAtras(View view){

        finish();
    }

    public void confirmarCompra(View view){

        if(cart.getTotalPrice() <= 0.0){

            totalPrice.setText("No se puede confirmar la compra.");
        } else{

            totalPrice.setText("Compra confirmada por $" + cart.getTotalPrice());
        }
    }

    private void cargarProductosCarrito(){

        String url = "https://animatiapp.up.railway.app/api/carritoProductos/lista/carrito/" + idCarrito;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {

                    try{

                        double total = 0.0;

                        //Limpio la lista para evitar datos repetidos
                        productNames.clear();

                        //Itero sobre el array de productos
                        for(int i = 0; i < response.length(); i++){

                            JSONObject productoCarrito = response.getJSONObject(i);

                            int id = productoCarrito.getInt("id");
                            String nombreProducto = productoCarrito.getString("Codigo");
                            double precio = productoCarrito.getDouble("Precio");
                            int cantidad = productoCarrito.getInt("Cantidad");

                            //Agrego el producto al carrito
                            cart.addProduct(new Product(id, nombreProducto, precio / cantidad, cantidad));

                            //Actualizo la lista de nombres de productos
                            String elementoProducto = nombreProducto + " - $" + precio / cantidad + " x " + cantidad + " (Total: " + precio + ")";
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
                    //Log.d("CartAct", error.getMessage());
                    Toast.makeText(CartActivity.this, "Carrito vacío.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRemoveClick(int position) {

        ArrayList<Product> products = cart.getProducts();
        int idProducto = products.get(position).getId();


        String url = "https://animatiapp.up.railway.app/api/carritoProductos/eliminar/" + idProducto;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, response -> {

            try{

                Double total = 0.0;

                String mensaje = response.getString("message");

                //Elimino el producto del carrito
                productNames.remove(position);
                cart.removeProduct(position);

                ArrayList<Product> newProducts = cart.getProducts();

                for(int i = 0; i < cart.getCarritoSize(); i++){

                    Double precio = newProducts.get(i).getPrice();
                    Double cantidad = (double) newProducts.get(i).getQuantity();
                    //Sumo el precio al total
                    total += precio * cantidad;
                }

                adapter.notifyDataSetChanged();

                //Muestro el precio total en el textView
                totalPrice.setText("Total: $" + total);


                Toast.makeText(CartActivity.this, mensaje, Toast.LENGTH_SHORT).show();

            } catch (JSONException e){

                e.printStackTrace();
            }
        },
                error -> {
                    //Log.d("CartAct", error.getMessage());
                    Toast.makeText(CartActivity.this, "Error al eliminar producto", Toast.LENGTH_SHORT).show();
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