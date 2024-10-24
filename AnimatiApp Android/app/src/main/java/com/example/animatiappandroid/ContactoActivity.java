package com.example.animatiappandroid;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ContactoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        Button submitButton = findViewById(R.id.btn_submit);

        submitButton.setOnClickListener(v -> {
            EditText nameField = findViewById(R.id.et_name);
            EditText emailField = findViewById(R.id.et_email);
            EditText messageField = findViewById(R.id.et_message);

            String name = nameField.getText().toString();
            String email = emailField.getText().toString();
            String message = messageField.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tu nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
                return;
            }

            if (message.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Formulario enviado con éxito", Toast.LENGTH_SHORT).show();

            nameField.setText("");
            emailField.setText("");
            messageField.setText("");
        });
    }
}
