package com.example.animatiappandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ModifyProfileActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private EditText usernameEditText, firstNameEditText, lastNameEditText, emailEditText;
    private Button saveButton, cancelButton;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        usernameTextView = findViewById(R.id.modify_user_name);
        usernameEditText = findViewById(R.id.modify_profile_usernameEditText);
        firstNameEditText = findViewById(R.id.modify_profile_firstNameEditText);
        lastNameEditText = findViewById(R.id.modify_profile_lastNameEditText);
        emailEditText = findViewById(R.id.modify_profile_emailEditText);
        saveButton = findViewById(R.id.modify_profile_button_accept);
        cancelButton = findViewById(R.id.modify_profile_button_cancel);

        sharedPreferences = getSharedPreferences("AnimatiPreferencias", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        requestQueue = Volley.newRequestQueue(this);

        saveButton.setOnClickListener(view -> actualizarPerfil());

        cancelButton.setOnClickListener(view -> finish());

        cargarDatosUsuario();
    }



    private void cargarDatosUsuario() {
        String url = "https://animatiapp.up.railway.app/api/perfilusuario";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            usernameTextView.setText(response.getString("username"));

                            usernameEditText.setText(response.getString("username"));
                            firstNameEditText.setText(response.getString("first_name"));
                            lastNameEditText.setText(response.getString("last_name"));
                            emailEditText.setText(response.getString("email"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ModifyProfileActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ModifyProfileActivity.this, "Error al obtener perfil: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("ModifyProfile", "Error perfil", error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        requestQueue.add(request);
    }


    private void actualizarPerfil() {
        String url = "https://animatiapp.up.railway.app/api/perfilusuario";

        JSONObject body = new JSONObject();
        try {
            body.put("username", usernameEditText.getText().toString().trim());
            body.put("first_name", firstNameEditText.getText().toString().trim());
            body.put("last_name", lastNameEditText.getText().toString().trim());
            body.put("email", emailEditText.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                body,
                response -> {
                    Toast.makeText(ModifyProfileActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    Toast.makeText(ModifyProfileActivity.this, "Error al actualizar: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("ModifyProfile", "Volley error: ", error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }
}
