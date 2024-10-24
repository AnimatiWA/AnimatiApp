package com.example.animatiappandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";  // Definimos una etiqueta para el registro
    private ImageView profileImage;
    private TextView userName;
    private Button changeProfileImageButton, changeEmailButton, changePasswordButton, viewPurchaseHistoryButton, viewOrderTrackingButton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        changeProfileImageButton = findViewById(R.id.change_profile_image_button);
        changeEmailButton = findViewById(R.id.change_email_button);
        changePasswordButton = findViewById(R.id.change_password_button);
        viewPurchaseHistoryButton = findViewById(R.id.view_purchase_history_button);
        viewOrderTrackingButton = findViewById(R.id.view_order_tracking_button);

        requestQueue = Volley.newRequestQueue(this);

        // Obtener datos del usuario de la base de datos y mostrar
        getUserData();

        // Cambiar imagen de perfil
        changeProfileImageButton.setOnClickListener(v -> {
            // Lógica para cambiar la imagen de perfil
            Toast.makeText(ProfileActivity.this, "Cambiar imagen de perfil", Toast.LENGTH_SHORT).show();
        });

        // Cambiar email
        changeEmailButton.setOnClickListener(v -> {
            // Lógica para cambiar el email
            Toast.makeText(ProfileActivity.this, "Cambiar email", Toast.LENGTH_SHORT).show();
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

    private void getUserData() {
        // Obtener el ID del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("idUsuario", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://animatiapp.up.railway.app/api/perfilusuario/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String firstName = response.getString("first_name");
                            String lastName = response.getString("last_name");
                            String welcomeMessage = getString(R.string.welcome_message, firstName, lastName);
                            userName.setText(welcomeMessage);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error al procesar la respuesta JSON", e);  // Registro robusto del error
                            Toast.makeText(ProfileActivity.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error en la solicitud GET", error);  // Registro robusto del error
                        Toast.makeText(ProfileActivity.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
