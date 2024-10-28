package com.example.animatiappandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button resetPasswordButton;
    private RequestQueue requestQueue;
    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Inicialización de las vistas
        emailEditText = findViewById(R.id.editTextEmail);
        resetPasswordButton = findViewById(R.id.buttonSend);

        // Inicialización de la cola de solicitudes de Volley
        requestQueue = Volley.newRequestQueue(this);

        // Listener para el botón de enviar
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (!email.isEmpty()) {
                    sendPasswordRecoveryRequest(email);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Por favor, introduce tu correo electrónico", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para enviar la solicitud de recuperación de contraseña
    private void sendPasswordRecoveryRequest(String email) {
        String url = "https://animatiapp.up.railway.app/api/password_recovery";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ForgotPasswordActivity.this, "Enlace de recuperación enviado a " + email, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "VolleyError: " + error.toString());
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String body;
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Log.e(TAG, "Server Error: " + body);
                                // Verificar si el mensaje de error contiene "Correo no encontrado"
                                if (body.contains("Correo no encontrado")) {
                                    Toast.makeText(ForgotPasswordActivity.this, "Correo electrónico no registrado", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this, "Error al enviar el enlace de recuperación", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing network response: " + e.getMessage());
                                Toast.makeText(ForgotPasswordActivity.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Error al enviar el enlace de recuperación", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}


