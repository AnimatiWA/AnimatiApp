package com.example.animatiappandroid;

import android.content.Intent;
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

        emailEditText = findViewById(R.id.editTextEmail);
        resetPasswordButton = findViewById(R.id.buttonSend);

        requestQueue = Volley.newRequestQueue(this);

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

    private void sendPasswordRecoveryRequest(String email) {
        String url = "https://animatiapp.up.railway.app/api/passwordRecovery";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email); // El cuerpo solo incluye el email
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
                        // Mostrar mensaje cuando se ha enviado el enlace
                        Toast.makeText(ForgotPasswordActivity.this, "Enlace de recuperación enviado a " + email, Toast.LENGTH_SHORT).show();

                        // Redirigir a ForgotPassEmailActivity
                        Intent intent = new Intent(ForgotPasswordActivity.this, ForgotPassEmailActivity.class);
                        startActivity(intent);
                        finish();  // Cerrar la actividad actual
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

