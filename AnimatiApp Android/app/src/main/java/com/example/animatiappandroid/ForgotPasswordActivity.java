package com.example.animatiappandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Importaciones Volley
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
        String url = "https://animatiapp.up.railway.app/api/password_recovery";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
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
                        Toast.makeText(ForgotPasswordActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
