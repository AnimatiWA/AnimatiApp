package com.example.animatiappandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";  // Definimos una etiqueta para el registro
    private ImageView profileImage;
    private TextView userName;
    private Button changeEmailButton, changePasswordButton, viewPurchaseHistoryButton, viewOrderTrackingButton, modifyProfileButton;
    private RequestQueue requestQueue;
    private SharedPreferences preferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        changeEmailButton = findViewById(R.id.change_email_button);
        changePasswordButton = findViewById(R.id.change_password_button);
        viewPurchaseHistoryButton = findViewById(R.id.view_purchase_history_button);
        viewOrderTrackingButton = findViewById(R.id.view_order_tracking_button);
        modifyProfileButton = findViewById(R.id.modify_profile_button);

        requestQueue = Volley.newRequestQueue(this);
        preferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);
        token = preferences.getString("token", "");

        // Obtener datos del usuario de la base de datos y mostrar
        getUserData();

        // Cambiar email
        changeEmailButton.setOnClickListener(v -> {
            // Lógica para cambiar el email
            Toast.makeText(ProfileActivity.this, "Cambiar email", Toast.LENGTH_SHORT).show();
        });

        // Cambiar contraseña
        changePasswordButton.setOnClickListener(v -> {
            // Navegar a la actividad de recuperación de contraseña
            Intent intent = new Intent(ProfileActivity.this, RecoveryPasswordActivity.class);
            startActivity(intent);
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

 
        Button adminButton = findViewById(R.id.admin_button);
        adminButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, AdminActivity.class);
            startActivity(intent);
        });
        // Modificar perfil de usuario
        modifyProfileButton.setOnClickListener(v -> {
            // Navegar a la actividad de modificacion perfil
            Intent intent = new Intent(ProfileActivity.this, ModifyProfileActivity.class);

            startActivity(intent);
        });
    }

    private void getUserData() {
        // Obtener el ID del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("idUser", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://animatiapp.up.railway.app/api/perfilusuario";
        Log.d(TAG, "URL de la solicitud: " + url);  // Log de la URL de la solicitud
        Log.d(TAG, "Token: " + token);  // Log del token de autenticación

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "Respuesta JSON: " + response.toString());
                            String firstName = response.getString("first_name");
                            //String lastName = response.getString("last_name");
                            String welcomeMessage;
                            // Verificar la última letra del firstName
                            if (firstName.endsWith("a")) {
                                welcomeMessage = "Bienvenida, " + firstName;
                            } else {
                                welcomeMessage = "Bienvenido, " + firstName;
                            }

                            userName.setText(welcomeMessage);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error al procesar la respuesta JSON", e);
                            Toast.makeText(ProfileActivity.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error en la solicitud GET", error);
                        Toast.makeText(ProfileActivity.this, "Error al obtener los datos: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                Log.d(TAG, "Headers: " + headers.toString());  // Log de los headers de la solicitud
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


    public void op_carrito(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    public void op_menu(View view) {
        Intent intent = new Intent(this, activity_inicio.class);
        startActivity(intent);
    }
}


