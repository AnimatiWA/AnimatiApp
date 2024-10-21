package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profileImage;
    private EditText userName, userAddress, userLocation, userPostalCode;
    private Button changeProfileImageButton, changeAddressButton, changePasswordButton, viewPurchaseHistoryButton, viewOrderTrackingButton;
    private ImageButton editAddressButton, editLocationButton, editPostalCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        userAddress = findViewById(R.id.user_address);
        userLocation = findViewById(R.id.user_location);
        userPostalCode = findViewById(R.id.user_postal_code);

        changeProfileImageButton = findViewById(R.id.change_profile_image_button);
        changeAddressButton = findViewById(R.id.change_address_button);
        changePasswordButton = findViewById(R.id.change_password_button);
        viewPurchaseHistoryButton = findViewById(R.id.view_purchase_history_button);
        viewOrderTrackingButton = findViewById(R.id.view_order_tracking_button);

        editAddressButton = findViewById(R.id.edit_address_button);
        editLocationButton = findViewById(R.id.edit_location_button);
        editPostalCodeButton = findViewById(R.id.edit_postal_code_button);

        // Cambiar imagen de perfil
        changeProfileImageButton.setOnClickListener(v -> {
            // Lógica para cambiar la imagen de perfil
            Toast.makeText(ProfileActivity.this, "Cambiar imagen de perfil", Toast.LENGTH_SHORT).show();
        });

        // Editar dirección
        editAddressButton.setOnClickListener(v -> {
            userAddress.setEnabled(true);
            Toast.makeText(ProfileActivity.this, "Editar dirección", Toast.LENGTH_SHORT).show();
        });

        // Editar localidad
        editLocationButton.setOnClickListener(v -> {
            userLocation.setEnabled(true);
            Toast.makeText(ProfileActivity.this, "Editar localidad", Toast.LENGTH_SHORT).show();
        });

        // Editar código postal
        editPostalCodeButton.setOnClickListener(v -> {
            userPostalCode.setEnabled(true);
            Toast.makeText(ProfileActivity.this, "Editar código postal", Toast.LENGTH_SHORT).show();
        });

        // Cambiar contraseña
        changePasswordButton.setOnClickListener(v -> {
            // Lógica para cambiar la contraseña
            Toast.makeText(ProfileActivity.this, "Cambiar contraseña", Toast.LENGTH_SHORT).show();
        });

        // Ver historial de compras
        viewPurchaseHistoryButton.setOnClickListener(v -> {
            // Navegar a la actividad de historial de compras
            Intent intent = new Intent(ProfileActivity.this, PurchaseHistoryActivity.class);
            startActivity(intent);
        });

        // Ver seguimiento de pedidos
        viewOrderTrackingButton.setOnClickListener(v -> {
            // Navegar a la actividad de seguimiento de pedidos
            Intent intent = new Intent(ProfileActivity.this, OrderTrackingActivity.class);
            startActivity(intent);
        });
    }
}
