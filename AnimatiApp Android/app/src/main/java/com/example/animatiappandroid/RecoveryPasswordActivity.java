package com.example.animatiappandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class RecoveryPasswordActivity extends AppCompatActivity {
    private Button goHomeButton, sendButton;
    private EditText newPasswordEditText, repeatPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_password);

        // Inicialización de las vistas
        goHomeButton = findViewById(R.id.buttonGoHome);
        sendButton = findViewById(R.id.buttonSend);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);

        // Listener para el botón "Volver al inicio"
        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar de vuelta a la pantalla de inicio de sesión
                Intent intent = new Intent(RecoveryPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Listener para el botón "Enviar"
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener las contraseñas introducidas por el usuario
                String newPassword = newPasswordEditText.getText().toString();
                String repeatPassword = repeatPasswordEditText.getText().toString();

                // Validar que las contraseñas coincidan y cumplan con los requisitos
                if (validatePasswords(newPassword, repeatPassword)) {
                    // Ejecutar la tarea de recuperación de contraseña
                    new RecoverPasswordTask(newPassword).execute();
                }
            }
        });
    }

    private boolean validatePasswords(String newPassword, String repeatPassword) {
        // Verificar si las contraseñas no coinciden
        if (!newPassword.equals(repeatPassword)) {
            showAlertDialog("Error", "Las contraseñas no coinciden.");
            return false;
        }

        // Validar que la contraseña cumpla con los requisitos
        if (!validatePassword(newPassword)) {
            showAlertDialog("Error", "La contraseña debe tener al menos 6 caracteres, una letra mayúscula, una letra minúscula y un número.");
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {
        // Patrón para validar la contraseña
        Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$");
        return passwordPattern.matcher(password).matches();
    }

    private void showAlertDialog(String title, String message) {
        // Mostrar un cuadro de diálogo de alerta
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void showSuccessDialog() {
        // Mostrar un cuadro de diálogo de éxito
        new AlertDialog.Builder(this)
                .setTitle("Éxito")
                .setMessage("Contraseña actualizada exitosamente.")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Navegar a la pantalla de inicio de sesión
                    Intent intent = new Intent(RecoveryPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                })
                .show();
    }

    private class RecoverPasswordTask extends AsyncTask<Void, Void, String> {
        private String newPassword;

        public RecoverPasswordTask(String newPassword) {
            this.newPassword = newPassword;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Obtener el ID del usuario y el token desde SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt("idUser", -1);
                String token = sharedPreferences.getString("token", "");

                if (userId == -1) {
                    return "ID de usuario no encontrado";
                }

                // Crear la URL para la solicitud al backend de Django
                URL url = new URL("https://animatiapp.up.railway.app/api/passwordRecovery");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "Bearer " + token);  // Enviar el token en el encabezado
                urlConnection.setDoOutput(true);

                // Crear el objeto JSON con los datos de la contraseña
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("password", newPassword);
                jsonParam.put("password2", newPassword);  // Asegurarse de que ambas contraseñas se envíen

                // Enviar los datos
                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                // Obtener la respuesta del servidor
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Contraseña actualizada con éxito";
                } else {
                    return "Error en la actualización de la contraseña: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Error de conexión";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Contraseña actualizada con éxito")) {
                showSuccessDialog();
            } else {
                showAlertDialog("Error", result);
            }
        }
    }
}

