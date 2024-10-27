package com.example.animatiappandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

public class ContactoActivity extends AppCompatActivity {

    private RequestQueue queue;
    SharedPreferences preferences;
    private String token;
    private EditText nameField, emailField, messageField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_contacto);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        queue = Volley.newRequestQueue(this);
        preferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);
        token = preferences.getString("token", "");

        nameField = findViewById(R.id.et_name);
        emailField = findViewById(R.id.et_email);
        messageField = findViewById(R.id.et_message);
        Button submitButton = findViewById(R.id.btn_submit);

        submitButton.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String message = messageField.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
                Toast.makeText(ContactoActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            } else {
                enviarSolicitudContacto(name, email, message);
            }
        });
    }

    public void op_carrito(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    public void op_menu(View view) {
        Intent intent = new Intent(this, activity_inicio.class);
        startActivity(intent);
    }

    private void enviarSolicitudContacto(String name, String email, String message) {
        String url = "https://animatiapp.up.railway.app/api/contacto";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nombre", name);
            jsonObject.put("email", email);
            jsonObject.put("mensaje", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    Toast.makeText(ContactoActivity.this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();
                    nameField.setText("");
                    emailField.setText("");
                    messageField.setText("");
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(ContactoActivity.this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show();
                    Log.e("ContactoActivity", "Error en la solicitud: " + error.getMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders(){

                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }}

