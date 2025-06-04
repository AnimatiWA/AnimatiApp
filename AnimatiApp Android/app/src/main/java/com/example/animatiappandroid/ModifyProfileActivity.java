package com.example.animatiappandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView currentProfileImage;
    private ImageView avatarSelect1, avatarSelect2, avatarSelect3, avatarSelect4, avatarSelect5, avatarSelect6, avatarSelect7, avatarSelect8, avatarSelect9;
    private static final String PREF_AVATAR_KEY = "selected_avatar_resource_name";
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

        currentProfileImage = findViewById(R.id.modify_profile_image);
        avatarSelect1 = findViewById(R.id.avatar_select1);
        avatarSelect2 = findViewById(R.id.avatar_select2);
        avatarSelect3 = findViewById(R.id.avatar_select3);
        avatarSelect4 = findViewById(R.id.avatar_select4);
        avatarSelect5 = findViewById(R.id.avatar_select5);
        avatarSelect6 = findViewById(R.id.avatar_select6);
        avatarSelect7 = findViewById(R.id.avatar_select7);
        avatarSelect8 = findViewById(R.id.avatar_select8);
        avatarSelect9 = findViewById(R.id.avatar_select9);

        sharedPreferences = getSharedPreferences("AnimatiPreferencias", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        requestQueue = Volley.newRequestQueue(this);

        saveButton.setOnClickListener(view -> actualizarPerfil());

        cancelButton.setOnClickListener(view -> finish());

        setupAvatarClickListeners();
        cargarDatosUsuario();
    }



    private void setupAvatarClickListeners() {
        avatarSelect1.setOnClickListener(v -> selectAvatar("avatar1", R.drawable.avatar1));
        avatarSelect2.setOnClickListener(v -> selectAvatar("avatar2", R.drawable.avatar2));
        avatarSelect3.setOnClickListener(v -> selectAvatar("avatar3", R.drawable.avatar3));
        avatarSelect4.setOnClickListener(v -> selectAvatar("avatar4", R.drawable.avatar4));
        avatarSelect5.setOnClickListener(v -> selectAvatar("avatar5", R.drawable.avatar5));
        avatarSelect6.setOnClickListener(v -> selectAvatar("avatar6", R.drawable.avatar6));
        avatarSelect7.setOnClickListener(v -> selectAvatar("avatar7", R.drawable.avatar7));
        avatarSelect8.setOnClickListener(v -> selectAvatar("avatar8", R.drawable.avatar8));
        avatarSelect9.setOnClickListener(v -> selectAvatar("avatar9", R.drawable.avatar9));
    }

    private void selectAvatar(String resourceName, int drawableId) {
        currentProfileImage.setImageResource(drawableId);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_AVATAR_KEY, resourceName);
        editor.apply();
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

        String savedAvatarResourceName = sharedPreferences.getString(PREF_AVATAR_KEY, null);
        if (savedAvatarResourceName != null) {
            int avatarResId = getResources().getIdentifier(savedAvatarResourceName, "drawable", getPackageName());
            if (avatarResId != 0) {
                currentProfileImage.setImageResource(avatarResId);
            } else {
                currentProfileImage.setImageResource(R.drawable.default_profile);
                Log.w("ModifyProfileActivity", "Saved avatar resource not found: " + savedAvatarResourceName);
            }
        } else {
            currentProfileImage.setImageResource(R.drawable.default_profile);
        }

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
                    Toast.makeText(ModifyProfileActivity.this, "Perfil correctamente", Toast.LENGTH_SHORT).show();
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
