package com.example.animatiappandroid;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.animatiappandroid.ProductListAdapter;

import android.content.Intent;
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
    private int idUser;
    private String token;
    private double total;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        queue = Volley.newRequestQueue(this);

        cart = new Cart();
        productNames = new ArrayList<>();

        preferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);
        idCarrito = preferences.getInt("idCarrito", -1);
        idUser = preferences.getInt("idUser", -1);
        token = preferences.getString("token", "");

        ListView productList = findViewById(R.id.product_list);
        Button confirmButton = findViewById(R.id.confirm_button);
        Button backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(this::volverAtras);
        confirmButton.setOnClickListener(this::confirmarCompra);

        if(idCarrito == -1){
            Toast.makeText(this, "Carrito vacio", Toast.LENGTH_LONG).show();
            return;
        }

        totalPrice = findViewById(R.id.total_price);

        cargarProductosCarrito();

        adapter = new ProductListAdapter(this, productNames, this);
        productList.setAdapter(adapter);
    }

    public void volverAtras(View view){

        finish();
    }

    public void confirmarCompra(View view){

        if(idCarrito == -1){
            Toast.makeText(this, "Carrito vacio", Toast.LENGTH_LONG).show();
            return;
        }

        if(cart.getTotalPrice() <= 0.0){

            totalPrice.setText("No se puede confirmar la compra.");
        } else{

            Intent intent = new Intent(this, PagoProvisionalActivity.class);

            intent.putExtra("total", cart.getTotalPrice());

            startActivity(intent);
        }
    }

    private void cargarProductosCarrito(){

        String url = "https://animatiapp.up.railway.app/api/carritoProductos/lista/carrito/" + idCarrito;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {

                    try{

                        total = 0.0;

                        productNames.clear();

                        for(int i = 0; i < response.length(); i++){

                            JSONObject productoCarrito = response.getJSONObject(i);

                            int id = productoCarrito.getInt("id");
                            int Codigo = productoCarrito.getInt("Codigo");
                            String nombreProducto = productoCarrito.getString("nombre_producto");
                            double precio = productoCarrito.getDouble("Precio");
                            int cantidad = productoCarrito.getInt("Cantidad");

                            double precioUnitario = precio / cantidad;
                            String precioFormateado = "$" + precioUnitario + " x " + cantidad + " unidades";
                            String precioTotal = "Total: $" + precio;

                            cart.addProduct(new Product(id, nombreProducto, precio / cantidad, cantidad, 0));

                            productNames.add(nombreProducto + "," + precioFormateado + "," + precioTotal);

                            total += precio;
                        }

                        adapter.notifyDataSetChanged();

                        totalPrice.setText("Total: $" + total);

                    } catch (JSONException e){

                        e.printStackTrace();
                    }
                },
                error -> {

                    Toast.makeText(CartActivity.this, "Carrito vacío.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders(){

                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }

    private void confirmarCompra(){

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

                        Toast.makeText(CartActivity.this, "Compra confirmada por $" + cart.getTotalPrice(), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(CartActivity.this, activity_inicio.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {

                        e.printStackTrace();
                        totalPrice.setText("Total: $" + total);
                        Toast.makeText(CartActivity.this, "Error al confirmar la compra", Toast.LENGTH_SHORT).show();
                    }



                }, error -> {

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

            Toast.makeText(CartActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders(){

                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
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

    @Override
    public void onRemoveOneClick(int position) {

        ArrayList<Product> products = cart.getProducts();
        int idProducto = products.get(position).getId();


        String url = "https://animatiapp.up.railway.app/api/carritoProductos/eliminarUnidad/" + idProducto;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, null, response -> {

            try{

                if(response.has("message")){

                    productNames.remove(position);
                    cart.removeProduct(position);

                    adapter.notifyDataSetChanged();

                    double total = cart.getTotalPrice();

                    totalPrice.setText("Total: $" + total);
                    Toast.makeText(CartActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                } else{

                    String nombreProducto = response.getString("nombre_producto");
                    double precio = response.getDouble("Precio");
                    int cantidad = response.getInt("Cantidad");

                    double precioUnitario = precio / cantidad;
                    String precioFormateado = "$" + precioUnitario + " x " + cantidad + " unidades";
                    String precioTotal = "Total: $" + precio;

                    productNames.set(position, nombreProducto + "," + precioFormateado + "," + precioTotal);

                    cart.getProducts().get(position).setQuantity(cantidad);
                    cart.getProducts().get(position).setPrice(precioUnitario);

                    adapter.notifyDataSetChanged();

                    double total = cart.getTotalPrice();
                    totalPrice.setText("Total: $" + total);
                }
            } catch (JSONException e) {

                e.printStackTrace();
                Toast.makeText(CartActivity.this, "Error al actualizar el producto", Toast.LENGTH_SHORT).show();
            }
        }, error -> {

            Toast.makeText(CartActivity.this, "Error al eliminar producto", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders(){

                Map<String, String> headers = new HashMap<>();

                //Agrego el token para la autorizacion del endpoint
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }
}