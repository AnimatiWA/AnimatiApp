package com.example.animatiappandroid;
import android.util.Log;
import com.android.volley.toolbox.JsonObjectRequest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    public RecyclerView adminRecyclerView;
    public AdminProductAdapter adapter;
    public List<Product> productList;
    public RequestQueue requestQueue;
    public String token;
    public Button addProductButton;
    public Button volverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Obtener token del usuario
        SharedPreferences preferences = getSharedPreferences("AnimatiPreferencias", MODE_PRIVATE);
        token = preferences.getString("token", "");


        adminRecyclerView = findViewById(R.id.admin_recycler_view);
        volverButton = findViewById(R.id.volver_button);
        addProductButton = findViewById(R.id.add_product_button);  // <--- Aquí está la línea que faltaba

        productList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        adminRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProductAdapter(this, productList, requestQueue, new AdminProductAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Product producto = productList.get(position);
                mostrarDialogoEditarProducto(producto, position);
            }

             @Override
             public void onDeleteClick(int position) {
                Product productToDelete = productList.get(position);
                int idProducto = productToDelete.getId();

                eliminarProducto(idProducto, position);
            }
            @Override
            public void onItemClick(int position) {
                // Código cuando se clickea el item
            }

        });


        adminRecyclerView.setAdapter(adapter);

        cargarProductosDesdeAPI();

        addProductButton.setOnClickListener(v -> mostrarDialogoAgregarProducto());

        volverButton.setOnClickListener(v -> {

            finish();
        });
    }

    private void eliminarProducto(int idProducto, int position) {
        // Recuperar token desde SharedPreferences
        SharedPreferences preferences = getSharedPreferences("AnimatiPreferencias", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Token vacío, por favor inicia sesión de nuevo.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear diálogo de confirmación
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de eliminar este producto?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Si confirma, hacer la petición para eliminar
                    String url = "https://animatiapp.up.railway.app/api/producto/eliminar/" + idProducto;

                    StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                            response -> {
                                productList.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                            },
                            error -> {
                                String mensajeError = "Error al eliminar producto";
                                if (error.networkResponse != null && error.networkResponse.data != null) {
                                    String errorResponse = new String(error.networkResponse.data);
                                    mensajeError += ": " + errorResponse;
                                }
                                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show();
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String,String> headers = new HashMap<>();
                            headers.put("Authorization", "Bearer " + token);
                            Log.d("EliminarProducto", "Headers: " + headers.toString());
                            return headers;
                        }
                    };

                    requestQueue.add(deleteRequest);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Si cancela, solo cerrar diálogo
                    dialog.dismiss();
                })
                .create()
                .show();
    }



    private void cargarProductosDesdeAPI() {
        String url = "https://animatiapp.up.railway.app/api/producto/lista";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    productList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject producto = response.getJSONObject(i);
                            int id = producto.getInt("Codigo_Producto");
                            String nombre = producto.getString("Nombre_Producto");
                            double precio = Double.parseDouble(producto.getString("Precio"));
                            int stock = producto.getInt("Stock");
                            String imagen = producto.getString("Imagen");

                            // 👇 Esta es la línea nueva importante
                            int idCategoria = producto.getInt("Id_Categoria");

                            // Suponiendo que tu constructor tiene ese orden de parámetros
                            Product p = new Product(id, nombre, precio, 1, idCategoria, stock, imagen);

                            productList.add(p);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(AdminActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(jsonArrayRequest);
    }


    private void mostrarDialogoAgregarProducto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar nuevo producto");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre del producto");
        layout.addView(inputNombre);

        final EditText inputPrecio = new EditText(this);
        inputPrecio.setHint("Precio");
        inputPrecio.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(inputPrecio);

        final EditText inputStock = new EditText(this);
        inputStock.setHint("Stock");
        inputStock.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputStock);

        final EditText inputImagen = new EditText(this);
        inputImagen.setHint("URL de la imagen");
        layout.addView(inputImagen);

        final EditText inputCategoria = new EditText(this);
        inputCategoria.setHint("Id Categoría");
        inputCategoria.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputCategoria);

        builder.setView(layout);

        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String nombre = inputNombre.getText().toString().trim();
            String precioStr = inputPrecio.getText().toString().trim();
            String stockStr = inputStock.getText().toString().trim();
            String imagen = inputImagen.getText().toString().trim();
            String categoriaStr = inputCategoria.getText().toString().trim();

            if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty() || imagen.isEmpty() || categoriaStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double precio;
            int stock;
            int categoria;
            try {
                precio = Double.parseDouble(precioStr);
                stock = Integer.parseInt(stockStr);
                categoria = Integer.parseInt(categoriaStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Precio, stock o categoría inválidos", Toast.LENGTH_SHORT).show();
                return;
            }

            agregarProductoAPI(nombre, imagen, precio, stock, categoria);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }


    private void agregarProductoAPI(String nombre, String imagen, double precio, int stock, int categoria) {
        String url = "https://animatiapp.up.railway.app/api/producto/crear";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Nombre_Producto", nombre);
            jsonBody.put("Imagen", imagen);
            jsonBody.put("Precio", String.valueOf(precio));
            jsonBody.put("Stock", stock);
            jsonBody.put("Id_Categoria", categoria);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("AgregarProducto", "URL: " + url);
        Log.d("AgregarProducto", "JSON Enviado: " + jsonBody.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    Log.d("AgregarProducto", "Respuesta exitosa: " + response.toString());
                    Toast.makeText(this, "Producto agregado con éxito", Toast.LENGTH_SHORT).show();
                    cargarProductosDesdeAPI();
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorBody = new String(error.networkResponse.data);
                        Log.e("AgregarProducto", "Error en la respuesta: " + errorBody);
                        Toast.makeText(this, "Error al agregar producto: " + errorBody, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("AgregarProducto", "Error desconocido: " + error.toString());
                        Toast.makeText(this, "Error al agregar producto", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void mostrarDialogoEditarProducto(Product producto, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar producto");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre del producto");
        inputNombre.setText(producto.getName());
        layout.addView(inputNombre);

        final EditText inputPrecio = new EditText(this);
        inputPrecio.setHint("Precio");
        inputPrecio.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputPrecio.setText(String.valueOf(producto.getPrice()));
        layout.addView(inputPrecio);

        final EditText inputStock = new EditText(this);
        inputStock.setHint("Stock");
        inputStock.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputStock.setText(String.valueOf(producto.getStock()));
        layout.addView(inputStock);

        final EditText inputImagen = new EditText(this);
        inputImagen.setHint("URL de la imagen");
        inputImagen.setText(producto.getImagen());
        layout.addView(inputImagen);

        final EditText inputCategoria = new EditText(this);
        inputCategoria.setHint("Id Categoría");
        inputCategoria.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputCategoria.setText(String.valueOf(producto.getCategoria())); // 👈 ESTA LÍNEA ES CLAVE
        layout.addView(inputCategoria);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = inputNombre.getText().toString().trim();
            String precioStr = inputPrecio.getText().toString().trim();
            String stockStr = inputStock.getText().toString().trim();
            String imagen = inputImagen.getText().toString().trim();
            String categoriaStr = inputCategoria.getText().toString().trim();

            if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty() || imagen.isEmpty() || categoriaStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double precio;
            int stock;
            int categoria;
            try {
                precio = Double.parseDouble(precioStr);
                stock = Integer.parseInt(stockStr);
                categoria = Integer.parseInt(categoriaStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Precio, stock o categoría inválidos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mostrar diálogo de confirmación antes de enviar
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmar edición")
                    .setMessage("¿Estás seguro de editar este producto?")
                    .setPositiveButton("Sí", (confirmDialog, whichConfirm) -> {
                        editarProductoAPI(producto.getId(), nombre, imagen, precio, stock, categoria, position);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }


    private void editarProductoAPI(int idProducto, String nombre, String imagen, double precio, int stock, int categoria, int position) {
        String url = "https://animatiapp.up.railway.app/api/producto/actualizar/" + idProducto;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Nombre_Producto", nombre);
            jsonBody.put("Imagen", imagen);
            jsonBody.put("Precio", String.valueOf(precio));
            jsonBody.put("Stock", stock);
            jsonBody.put("Id_Categoria", categoria);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("EditarProducto", "URL: " + url);
        Log.d("EditarProducto", "JSON Enviado: " + jsonBody.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    Log.d("EditarProducto", "Respuesta exitosa: " + response.toString());
                    Toast.makeText(this, "Producto editado con éxito", Toast.LENGTH_SHORT).show();


                    Product productoEditado = productList.get(position);


                    productoEditado.setName(nombre);
                    productoEditado.setPrice(precio);
                    productoEditado.setStock(stock);
                    productoEditado.setImagen(imagen);
                    adapter.notifyItemChanged(position);
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorBody = new String(error.networkResponse.data);
                        Log.e("EditarProducto", "Error en la respuesta: " + errorBody);
                        Toast.makeText(this, "Error al editar producto: " + errorBody, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("EditarProducto", "Error desconocido: " + error.toString());
                        Toast.makeText(this, "Error al editar producto", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

}