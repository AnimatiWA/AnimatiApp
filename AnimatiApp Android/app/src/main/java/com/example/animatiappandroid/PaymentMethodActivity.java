package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentMethodActivity extends AppCompatActivity {

    private int idCarrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra);

        // Obtener el idCarrito desde el Intent
        idCarrito = getIntent().getIntExtra("idCarrito", -1);

        // Si no se recibe un idCarrito válido, mostrar un mensaje de error
        if (idCarrito == -1) {
            Toast.makeText(this, "Carrito vacío o no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Verificar si el carrito tiene productos cargados
        Cart cart = Cart.getInstance();
        if (cart.getCarritoSize() == 0) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Mostrar información del carrito (opcional, solo para ejemplo)
        TextView cartInfoTextView = findViewById(R.id.cartInfoTextView);
        StringBuilder cartDetails = new StringBuilder("Productos en el carrito:\n");

        // Agregar productos a la información que se mostrará
        for (Product product : cart.getProducts()) {
            cartDetails.append(product.getName())
                    .append(" - $")
                    .append(product.getPrice())
                    .append(" x ")
                    .append(product.getQuantity())
                    .append("\n");
        }

        cartInfoTextView.setText(cartDetails.toString());

        // Configurar los botones de las opciones de pago
        findViewById(R.id.textViewCreditCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCuotasActivity(idCarrito); // Pasar idCarrito a la siguiente actividad
            }
        });

        findViewById(R.id.textViewDebitCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNumerosTarjetaActivity(idCarrito); // Pasar idCarrito a la siguiente actividad
            }
        });
    }

    private void navigateToCuotasActivity(int idCarrito) {
        // Crear un Intent para navegar a la actividad de Cuotas
        Intent intent = new Intent(PaymentMethodActivity.this, CuotasActivity.class);
        intent.putExtra("idCarrito", idCarrito); // Pasar idCarrito
        startActivity(intent);
    }

    private void navigateToNumerosTarjetaActivity(int idCarrito) {
        // Crear un Intent para navegar a la actividad de NumerosTarjeta
        Intent intent = new Intent(PaymentMethodActivity.this, NumerosTarjetaActivity.class);
        intent.putExtra("idCarrito", idCarrito); // Pasar idCarrito
        startActivity(intent);
    }
}