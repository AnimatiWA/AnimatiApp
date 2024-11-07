package com.example.animatiappandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private ImageView profileImage;
    private TextView userName, purchaseItemName, purchaseDate, purchaseStatus;
    private Button changeEmailButton, changePasswordButton, viewPurchaseHistoryButton, viewOrderTrackingButton;
    private LinearLayout lastPurchaseDetails;
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
        lastPurchaseDetails = findViewById(R.id.last_purchase_details);
        purchaseItemName = findViewById(R.id.purchase_item_name);
        purchaseDate = findViewById(R.id.purchase_date);
        purchaseStatus = findViewById(R.id.purchase_status);

        requestQueue = Volley.newRequestQueue(this);
        preferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);
        token = preferences.getString("token", "");

        // Obtener datos del usuario de la base de datos y mostrar
        getUserData();

        // Obtener los datos de la última compra y mostrarlos
        getLastPurchaseData();

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
            // Mostrar u ocultar los detalles de la última compra
            if (lastPurchaseDetails.getVisibility() == View.GONE) {
                lastPurchaseDetails.setVisibility(View.VISIBLE);
            } else {
                lastPurchaseDetails.setVisibility(View.GONE);
            }
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

        String url = "https://animatiapp.up.railway.app/api/perfilusuario/" + userId;
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
                            Log.d(TAG, "Respuesta JSON: " + response.toString());  // Log de la respuesta JSON
                            String firstName = response.getString("first_name");
                            String lastName = response.getString("last_name");
                            String welcomeMessage;
                            // Verificar la última letra del firstName
                            if (firstName.endsWith("a")) {
                                welcomeMessage = "Bienvenida, " + firstName + " " + lastName;
                            } else {
                                welcomeMessage = "Bienvenido, " + firstName + " " + lastName;
                            }

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

    private void getLastPurchaseData() {
        String url = "https://animatiapp.up.railway.app/api/carrito/historial";
        Log.d(TAG, "URL de la solicitud: " + url);  // Log de la URL de la solicitud

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, "Respuesta JSON: " + response.toString());  // Log de la respuesta JSON
                            if (response.length() > 0) {
                                JSONObject lastPurchase = response.getJSONObject(0);  // Toma el primer objeto del arreglo
                                int id = lastPurchase.getInt("id");
                                String created = lastPurchase.getString("Creado");
                                boolean isActive = lastPurchase.getBoolean("is_active");
                                int userId = lastPurchase.getInt("Usuario");

                                purchaseItemName.setText("ID de la compra: " + id);
                                purchaseDate.setText("Fecha de compra: " + created);
                                purchaseStatus.setText("Estado: " + (isActive ? "Activo" : "Inactivo"));
                            } else {
                                Toast.makeText(ProfileActivity.this, "No se encontraron compras", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error al procesar la respuesta JSON", e);  // Registro robusto del error
                            Toast.makeText(ProfileActivity.this, "Error al procesar la respuesta JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error en la solicitud GET: " + error.toString());  // Registro robusto del error
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Código de respuesta HTTP: " + error.networkResponse.statusCode);  // Código de respuesta HTTP
                            try {
                                String body = new String(error.networkResponse.data, "UTF-8");
                                Log.e(TAG, "Cuerpo de la respuesta de error: " + body);  // Cuerpo de la respuesta de error
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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

        requestQueue.add(jsonArrayRequest);
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
