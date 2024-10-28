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
    private String userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_password);

        // Inicialización de las vistas
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        buttonSend = findViewById(R.id.buttonSend);

        userId = getIntent().getStringExtra("user_id");
        token = getIntent().getStringExtra("token");

        // Listener para el botón de enviar
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

    // Método para enviar la solicitud de actualización de contraseña
    private void resetPassword(String newPassword) {
        String url = "https://animatiapp.up.railway.app/reset_password/";  // Reemplaza con la URL real de tu API
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Mostrar un mensaje de éxito al actualizar la contraseña
                        Toast.makeText(RecoveryPasswordActivity.this, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Mostrar un mensaje de error al actualizar la contraseña
                Toast.makeText(RecoveryPasswordActivity.this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("new_password", newPassword);
                params.put("token", token);
                params.put("user_id", userId);
                return params;
            }
        };
        
        requestQueue.add(stringRequest);
    }
}


