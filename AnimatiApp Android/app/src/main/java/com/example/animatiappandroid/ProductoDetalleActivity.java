package com.example.animatiappandroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ProductoDetalleActivity extends AppCompatActivity {
    
    private ImageView ivProductoDetalle;
    private TextView tvNombreProducto;
    private TextView tvPrecioProducto;
    private TextView tvStockProducto;
    private TextView tvDescripcionProducto;
    private TextView tvCantidad;
    private Button btnAgregarCarrito;
    private ImageButton btnVolver;
    private ImageButton btnAumentar;
    private ImageButton btnDisminuir;
    
    private int productoId;
    private String productoNombre;
    private double productoPrecio;
    private int productoStock;
    private String productoDescripcion;
    private String productoImagen;
    private int productoCategoria;
    private int cantidad = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalle);
        
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        
        ivProductoDetalle = findViewById(R.id.ivProductoDetalle);
        tvNombreProducto = findViewById(R.id.tvNombreProducto);
        tvPrecioProducto = findViewById(R.id.tvPrecioProducto);
        tvStockProducto = findViewById(R.id.tvStockProducto);
        tvDescripcionProducto = findViewById(R.id.tvDescripcionProducto);
        tvCantidad = findViewById(R.id.tvCantidad);
        btnAgregarCarrito = findViewById(R.id.btnAgregarCarrito);
        btnVolver = findViewById(R.id.btnVolver);
        btnAumentar = findViewById(R.id.btnAumentar);
        btnDisminuir = findViewById(R.id.btnDisminuir);
        
        Intent intent = getIntent();
        productoId = intent.getIntExtra("producto_id", 0);
        productoNombre = intent.getStringExtra("producto_nombre");
        productoPrecio = intent.getDoubleExtra("producto_precio", 0.0);
        productoStock = intent.getIntExtra("producto_stock", 0);
        productoDescripcion = intent.getStringExtra("producto_descripcion");
        productoImagen = intent.getStringExtra("producto_imagen");
        productoCategoria = intent.getIntExtra("producto_categoria", 0);
        
        tvNombreProducto.setText(productoNombre);
        tvPrecioProducto.setText("$" + productoPrecio);
        tvStockProducto.setText("En stock: " + productoStock);
        tvDescripcionProducto.setText(productoDescripcion != null && !productoDescripcion.isEmpty() 
                ? productoDescripcion 
                : "No hay descripción disponible para este producto.");
        
        if (productoImagen != null && !productoImagen.isEmpty()) {
            Glide.with(this)
                    .load(productoImagen)
                    .placeholder(R.drawable.imagen_placeholder)
                    .error(R.drawable.imagen_error)
                    .into(ivProductoDetalle);
        }
        
        btnVolver.setOnClickListener(v -> onBackPressed());
        
        btnAumentar.setOnClickListener(v -> {
            if (cantidad < productoStock) {
                cantidad++;
                actualizarCantidad();
            } else {
                Toast.makeText(this, "No hay más stock disponible", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnDisminuir.setOnClickListener(v -> {
            if (cantidad > 1) {
                cantidad--;
                actualizarCantidad();
            }
        });
        
        actualizarCantidad();
        
        btnAgregarCarrito.setOnClickListener(v -> {
            // Volver a Gallery y simular un clic en el botón de agregar al carrito
            Intent resultIntent = new Intent();
            resultIntent.putExtra("action", "add_to_cart");
            resultIntent.putExtra("producto_id", productoId);
            resultIntent.putExtra("producto_cantidad", cantidad);
            setResult(RESULT_OK, resultIntent);
            
            // Solo mostramos mensaje pero no agregamos aquí, la Gallery se encargará de esto
            Toast.makeText(this, "Volviendo a la galería...", Toast.LENGTH_SHORT).show();
            
            finish();
        });
    }
    
    private void actualizarCantidad() {
        tvCantidad.setText(String.valueOf(cantidad));
    }
}
