package com.example.animatiappandroid;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RecoveryPasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText, repeatPasswordEditText;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_password);

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        buttonSend = findViewById(R.id.buttonSend);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordEditText.getText().toString();
                String repeatPassword = repeatPasswordEditText.getText().toString();

                if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(repeatPassword)) {
                    Toast.makeText(RecoveryPasswordActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(repeatPassword)) {
                    Toast.makeText(RecoveryPasswordActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                } else {
                    resetPassword(newPassword);
                }
            }
        });
    }

    private void resetPassword(String newPassword) {
        String url = "TU_URL_API/reset_password";  // Reemplaza con la URL real de tu API

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RecoveryPasswordActivity.this, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RecoveryPasswordActivity.this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("password", newPassword);
                // Aquí puedes añadir otros parámetros necesarios, como el token de recuperación
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}

