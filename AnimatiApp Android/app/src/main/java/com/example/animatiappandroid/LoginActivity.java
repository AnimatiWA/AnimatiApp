package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity<activity_login> extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;

    // credentials
    private final String validUsername = "user";
    private final String validPassword = "password123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        editTextUsername = findViewById(R.id.editTextText2);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        Button loginButton = findViewById(R.id.button);


        loginButton.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            if (validateInputs(username, password)) {
                if (username.equals(validUsername) && password.equals(validPassword)) {

                    Intent intent = new Intent(LoginActivity.this, activity_inicio.class);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        Button forgotPasswordButton = findViewById(R.id.forgotPassword);  // Button en lugar de TextView

        forgotPasswordButton.setOnClickListener(v -> {
            // Mostrar mensaje emergente
            Toast.makeText(LoginActivity.this, "Redirigiendo a recuperación de contraseña...", Toast.LENGTH_SHORT).show();

            // Redirigir a ForgotPasswordActivity
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }
}
