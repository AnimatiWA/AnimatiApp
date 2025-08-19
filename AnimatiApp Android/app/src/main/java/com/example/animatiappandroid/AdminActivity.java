package com.example.animatiappandroid;
import android.util.Log;
import com.android.volley.toolbox.JsonObjectRequest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
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
    public List<ProductAdmin> productList;
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
                ProductAdmin  producto = productList.get(position);
                mostrarDialogoEditarProducto(producto, position);
            }

             @Override
             public void onDeleteClick(int position) {
                 ProductAdmin  productToDelete = productList.get(position);
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
        
        // Cargar las categorías disponibles al iniciar
        cargarCategoriasDisponibles(false);
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
                            int idCategoria = producto.getInt("Id_Categoria");
                            
                            // Obtener descripción si existe
                            String descripcion = "";
                            if (producto.has("Descripcion")) {
                                descripcion = producto.getString("Descripcion");
                            }

                            // Construir objeto con descripción
                            ProductAdmin p = new ProductAdmin(id, nombre, precio, 1, idCategoria, stock, imagen, descripcion);


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


    // Listas para almacenar las categorías válidas
    private List<Integer> categoriasValidas = new ArrayList<>();
    private List<String> nombresCategoria = new ArrayList<>();
    private Map<String, Integer> mapaCategoriasId = new HashMap<>();
    
    // Lista de categorías disponibles en el sistema
    private void cargarCategoriasDisponibles(boolean mostrarToast) {
        Log.d("Categorias", "Iniciando carga de categorías disponibles...");
        String url = "https://animatiapp.up.railway.app/api/categoria/lista";
        
        // Para depuración - mostrar las categorías antes de limpiar
        Log.d("Categorias", "Estado actual de categorías válidas antes de actualizar: " + categoriasValidas.toString());
        
        // Limpiar las listas de categorías antes de cargar nuevas
        categoriasValidas.clear();
        nombresCategoria.clear();
        mapaCategoriasId.clear();
        
        // Por ahora, agregar manualmente las categorías que sabemos que existen
        // Estas se usarán como fallback si la API falla
        categoriasValidas.add(1);
        categoriasValidas.add(2);
        categoriasValidas.add(3);
        categoriasValidas.add(4);
        
        // Agregar nombres de fallback
        nombresCategoria.add("Categoría 1");
        nombresCategoria.add("Categoría 2");
        nombresCategoria.add("Categoría 3");
        nombresCategoria.add("Categoría 4");
        
        // Asociar nombres con IDs
        mapaCategoriasId.put("Categoría 1", 1);
        mapaCategoriasId.put("Categoría 2", 2);
        mapaCategoriasId.put("Categoría 3", 3);
        mapaCategoriasId.put("Categoría 4", 4);
        
        Log.d("Categorias", "Categorías fijas agregadas: " + categoriasValidas.toString());
        
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.d("Categorias", "Respuesta recibida del servidor: " + response.toString());
                        
                        // Limpiar nuevamente para evitar duplicados
                        categoriasValidas.clear();
                        nombresCategoria.clear();
                        mapaCategoriasId.clear();
                        
                        StringBuilder categoriasInfo = new StringBuilder("Categorías disponibles:\n");
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject categoria = response.getJSONObject(i);
                            Log.d("Categorias", "Procesando categoría: " + categoria.toString());
                            int id = categoria.getInt("Id_Categoria");
                            String nombre = categoria.getString("Nombre_Categoria");
                            categoriasInfo.append(id).append(": ").append(nombre).append("\n");
                            
                            // Guardar el ID y nombre de categoría
                            categoriasValidas.add(id);
                            String nombreCompleto = nombre + " (ID: " + id + ")";  // Añadir el ID al nombre mostrado
                            nombresCategoria.add(nombreCompleto);
                            mapaCategoriasId.put(nombreCompleto, id);
                            
                            Log.d("Categorias", "Categoría agregada: " + id + " - " + nombre);
                        }
                        
                        if (mostrarToast) {
                            Toast.makeText(this, categoriasInfo.toString(), Toast.LENGTH_LONG).show();
                        }
                        Log.d("Categorias", categoriasInfo.toString());
                        Log.d("Categorias", "IDs válidos finales: " + categoriasValidas.toString());
                        Log.d("Categorias", "Nombres de categorías: " + nombresCategoria.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Categorias", "Error al procesar JSON: " + e.getMessage());
                        if (mostrarToast) {
                            Toast.makeText(this, "Error al cargar categorías: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    Log.e("Categorias", "Error de red: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("Categorias", "Código de error: " + error.networkResponse.statusCode);
                    }
                    if (mostrarToast) {
                        Toast.makeText(this, "Error al cargar categorías: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        
        // Establecer timeout largo para evitar problemas de conexión
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,  // 15 segundos de timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        
        Log.d("Categorias", "Enviando solicitud de categorías a: " + url);
        requestQueue.add(jsonArrayRequest);
    }
    
    // Verificar si una categoría es válida
    private boolean esCategoriaValida(int categoriaId) {
        return categoriasValidas.contains(categoriaId);
    }

    private void mostrarDialogoAgregarProducto() {
        // Primero mostramos las categorías disponibles y esperamos a que terminen de cargarse
        cargarCategoriasDisponibles(false);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar nuevo producto");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre del producto");
        layout.addView(inputNombre);

        final EditText inputImagen = new EditText(this);
        inputImagen.setHint("URL de la imagen");
        layout.addView(inputImagen);

        final EditText inputPrecio = new EditText(this);
        inputPrecio.setHint("Precio");
        inputPrecio.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(inputPrecio);

        final EditText inputStock = new EditText(this);
        inputStock.setHint("Stock");
        inputStock.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputStock);
        
        // Crear un TextView para la etiqueta de categoría
        TextView categoriaLabel = new TextView(this);
        categoriaLabel.setText("Selecciona una categoría:");
        categoriaLabel.setPadding(0, 20, 0, 5);
        layout.addView(categoriaLabel);

        // Crear un Spinner (desplegable) para seleccionar la categoría
        final Spinner spinnerCategoria = new Spinner(this);
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nombresCategoria);
        spinnerCategoria.setAdapter(categoriaAdapter);
        layout.addView(spinnerCategoria);
        
        // Mostrar mensaje informativo sobre las categorías
        TextView categoriasInfo = new TextView(this);
        categoriasInfo.setText("\nNota: Si no ves categorías en el desplegable, inténtalo de nuevo en unos segundos.");
        categoriasInfo.setTextSize(12);
        categoriasInfo.setTextColor(Color.GRAY);
        layout.addView(categoriasInfo);

        final EditText inputDescripcion = new EditText(this);
        inputDescripcion.setHint("Descripción del producto");
        inputDescripcion.setPadding(0, 20, 0, 0);
        layout.addView(inputDescripcion);

        builder.setView(layout);

        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String nombre = inputNombre.getText().toString().trim();
            String precioStr = inputPrecio.getText().toString().trim();
            String stockStr = inputStock.getText().toString().trim();
            String imagen = inputImagen.getText().toString().trim();
            String descripcion = inputDescripcion.getText().toString().trim();
            
            // Obtener la categoría del spinner
            if (spinnerCategoria.getSelectedItem() == null || nombresCategoria.isEmpty()) {
                Toast.makeText(this, "Error: No hay categorías disponibles. Intenta nuevamente.", Toast.LENGTH_LONG).show();
                return;
            }
            
            String categoriaSeleccionada = spinnerCategoria.getSelectedItem().toString();
            Integer categoriaId = mapaCategoriasId.get(categoriaSeleccionada);
            
            if (categoriaId == null) {
                Toast.makeText(this, "Error: Categoría no válida", Toast.LENGTH_SHORT).show();
                Log.e("AgregarProducto", "No se pudo obtener ID para la categoría: " + categoriaSeleccionada);
                return;
            }

            if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty() || imagen.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            double precio;
            int stock;
            try {
                precio = Double.parseDouble(precioStr);
                stock = Integer.parseInt(stockStr);
                
                Log.d("AgregarProducto", "Usando categoría: " + categoriaId + " de " + categoriaSeleccionada);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Precio o stock inválidos", Toast.LENGTH_SHORT).show();
                return;
            }

            agregarProductoAPI(nombre, imagen, precio, stock, categoriaId, descripcion);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }


    private void agregarProductoAPI(String nombre, String imagen, double precio, int stock, int categoria, String descripcion) {
        String url = "https://animatiapp.up.railway.app/api/producto/crear";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Nombre_Producto", nombre);
            jsonBody.put("Imagen", imagen);
            jsonBody.put("Precio", String.valueOf(precio));
            jsonBody.put("Stock", stock);
            jsonBody.put("Id_Categoria", categoria);
            jsonBody.put("Descripcion", descripcion);
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
                    
                    // Recargamos la lista para asegurarnos de obtener todos los productos actualizados
                    // incluyendo el ID asignado por el servidor
                    Log.d("AgregarProducto", "Recargando lista de productos después de agregar");
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

    private void mostrarDialogoEditarProducto(ProductAdmin  producto, int position) {
        // Asegurar que tenemos las categorías cargadas
        cargarCategoriasDisponibles(false);
        
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

        // Crear un TextView para la etiqueta de categoría
        TextView categoriaLabel = new TextView(this);
        categoriaLabel.setText("Selecciona una categoría:");
        categoriaLabel.setPadding(0, 20, 0, 5);
        layout.addView(categoriaLabel);

        // Crear un Spinner para seleccionar la categoría
        final Spinner spinnerCategoria = new Spinner(this);
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nombresCategoria);
        spinnerCategoria.setAdapter(categoriaAdapter);
        layout.addView(spinnerCategoria);
        
        // Seleccionar la categoría actual del producto en el spinner
        int categoriaActual = producto.getCategoria();
        int posicionEnSpinner = 0;
        for (int i = 0; i < nombresCategoria.size(); i++) {
            String nombreCategoria = nombresCategoria.get(i);
            Integer idCategoria = mapaCategoriasId.get(nombreCategoria);
            if (idCategoria != null && idCategoria == categoriaActual) {
                posicionEnSpinner = i;
                break;
            }
        }
        spinnerCategoria.setSelection(posicionEnSpinner);
        
        // Mostrar mensaje informativo sobre las categorías
        TextView categoriasInfo = new TextView(this);
        categoriasInfo.setText("\nNota: Si no ves categorías en el desplegable, intenta nuevamente.");
        categoriasInfo.setTextSize(12);
        categoriasInfo.setTextColor(Color.GRAY);
        layout.addView(categoriasInfo);
        
        final EditText inputDescripcion = new EditText(this);
        inputDescripcion.setHint("Descripción del producto");
        inputDescripcion.setPadding(0, 20, 0, 0);
        if (producto.getDescription() != null) {
            inputDescripcion.setText(producto.getDescription());
        }
        layout.addView(inputDescripcion);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = inputNombre.getText().toString().trim();
            String precioStr = inputPrecio.getText().toString().trim();
            String stockStr = inputStock.getText().toString().trim();
            String imagen = inputImagen.getText().toString().trim();
            String descripcion = inputDescripcion.getText().toString().trim();

            // Obtener la categoría del spinner
            if (spinnerCategoria.getSelectedItem() == null || nombresCategoria.isEmpty()) {
                Toast.makeText(this, "Error: No hay categorías disponibles. Intenta nuevamente.", Toast.LENGTH_LONG).show();
                return;
            }
            
            String categoriaSeleccionada = spinnerCategoria.getSelectedItem().toString();
            Integer categoriaId = mapaCategoriasId.get(categoriaSeleccionada);
            
            if (categoriaId == null) {
                Toast.makeText(this, "Error: Categoría no válida", Toast.LENGTH_SHORT).show();
                Log.e("EditarProducto", "No se pudo obtener ID para la categoría: " + categoriaSeleccionada);
                return;
            }

            if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty() || imagen.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            double precio;
            int stock;
            try {
                precio = Double.parseDouble(precioStr);
                stock = Integer.parseInt(stockStr);
                
                Log.d("EditarProducto", "Usando categoría: " + categoriaId + " de " + categoriaSeleccionada);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Precio o stock inválidos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mostrar diálogo de confirmación antes de enviar
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmar edición")
                    .setMessage("¿Estás seguro de editar este producto?")
                    .setPositiveButton("Sí", (confirmDialog, whichConfirm) -> {
                        editarProductoAPI(producto.getId(), nombre, imagen, precio, stock, categoriaId, descripcion, position);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });


        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }


    private void editarProductoAPI(int idProducto, String nombre, String imagen, double precio, int stock, int categoria, String descripcion, int position) {
        String url = "https://animatiapp.up.railway.app/api/producto/actualizar/" + idProducto;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Nombre_Producto", nombre);
            jsonBody.put("Imagen", imagen);
            jsonBody.put("Precio", String.valueOf(precio));
            jsonBody.put("Stock", stock);
            jsonBody.put("Id_Categoria", categoria);
            jsonBody.put("Descripcion", descripcion);
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


                    ProductAdmin  productoEditado = productList.get(position);


                    productoEditado.setName(nombre);
                    productoEditado.setPrice(precio);
                    productoEditado.setStock(stock);
                    productoEditado.setCategoria(categoria);
                    productoEditado.setDescription(descripcion);


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