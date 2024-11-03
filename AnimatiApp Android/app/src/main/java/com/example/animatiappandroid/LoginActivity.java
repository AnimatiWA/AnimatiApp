package com.example.animatiappandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final int MAX_ATTEMPTS = 5;
    private static final String PREFERENCES_NAME = "AnimatiPreferencias";
    private static final String ATTEMPT_COUNT = "attemptCount";
    private static final String LAST_ATTEMPT_TIME = "lastAttemptTime";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private RequestQueue requestQueue;
    private int attemptCount;
    private long lastAttemptTime;

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
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        attemptCount = sharedPreferences.getInt(ATTEMPT_COUNT, 0);
        lastAttemptTime = sharedPreferences.getLong(LAST_ATTEMPT_TIME, 0);

        loginButton.setOnClickListener(v -> {
            if (System.currentTimeMillis() - lastAttemptTime < getWaitTime()) {
                long remainingTime = (getWaitTime() - (System.currentTimeMillis() - lastAttemptTime)) / 1000;
                Toast.makeText(LoginActivity.this, "Por favor, espera " + remainingTime + " segundos antes de intentar nuevamente.", Toast.LENGTH_SHORT).show();
                return;
            }

            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            if (validateInputs(username, password)) {
                loginUser(username, password);
            } else {
                Toast.makeText(LoginActivity.this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        forgotPasswordButton.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Redirigiendo a recuperación de contraseña...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        registerTextView.setOnClickListener(v -> {
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
                response -> handleSuccessfulLogin(response),
                error -> handleFailedLogin()
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void handleSuccessfulLogin(JSONObject response) {
        resetLoginAttempts();
        try {
            String token = response.getString("token");
            int carritoId = response.getInt("carrito");
            JSONObject userData = response.getJSONObject("user");
            int userId = userData.getInt("id");

            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", token);
            editor.putInt("idUser", userId);
            editor.putInt("idCarrito", carritoId);
            editor.apply();

            Toast.makeText(LoginActivity.this, "Sesion iniciada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, activity_inicio.class);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFailedLogin() {
        attemptCount++;
        long currentTime = System.currentTimeMillis();

        if (attemptCount >= MAX_ATTEMPTS) {
            lastAttemptTime = currentTime;
            Toast.makeText(LoginActivity.this, "Demasiados intentos fallidos. Por favor, espera " + getWaitTime() / 1000 + " segundos.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoginActivity.this, "Credenciales incorrectas. Intento " + attemptCount + " de " + MAX_ATTEMPTS, Toast.LENGTH_SHORT).show();
        }

        saveLoginAttempts();
    }

    private void resetLoginAttempts() {
        attemptCount = 0;
        lastAttemptTime = 0;
        saveLoginAttempts();
    }

    private void saveLoginAttempts() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ATTEMPT_COUNT, attemptCount);
        editor.putLong(LAST_ATTEMPT_TIME, lastAttemptTime);
        editor.apply();
    }

    private long getWaitTime() {
        return 60000 + (attemptCount - MAX_ATTEMPTS) * 300000;
    }
}
