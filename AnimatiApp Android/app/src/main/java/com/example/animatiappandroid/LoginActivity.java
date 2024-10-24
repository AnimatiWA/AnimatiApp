package com.example.animatiappandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextText2);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        Button loginButton = findViewById(R.id.button);
        Button forgotPasswordButton = findViewById(R.id.forgotPassword);
        TextView registerTextView = findViewById(R.id.registerTextView);


        requestQueue = Volley.newRequestQueue(this);

        loginButton.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            if (validateInputs(username, password)) {
                loginUser(username, password);
            } else {
                Toast.makeText(LoginActivity.this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        forgotPasswordButton.setOnClickListener(v -> {
            // Mostrar mensaje emergente
            Toast.makeText(LoginActivity.this, "Redirigiendo a recuperación de contraseña...", Toast.LENGTH_SHORT).show();

            // Redirigir a ForgotPasswordActivity
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Agregar el TextView para la navegación al registro
        registerTextView.setOnClickListener(v -> {
            // Redirigir a la actividad de registro
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    private void loginUser(String username, String password) {
        String url = "https://animatiapp.up.railway.app/api/login";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try {
                        String token = response.getString("token");
                        JSONObject userData = response.getJSONObject("user");
                        String userId = userData.getString("id");

                        // Almacenar en SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("AnimatiPreferencias", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.putString("idUser", userId);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Sesion iniciada").show();
                        Intent intent = new Intent(LoginActivity.this, activity_inicio.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Manejar el caso de credenciales incorrectas
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
