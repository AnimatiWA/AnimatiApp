package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;

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
                    // Aquí puedes añadir la lógica de recuperación de contraseña
                    showSuccessDialog();
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
}
