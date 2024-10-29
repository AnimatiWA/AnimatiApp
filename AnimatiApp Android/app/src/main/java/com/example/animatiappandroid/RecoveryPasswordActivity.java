package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RecoveryPasswordActivity extends AppCompatActivity {
    private Button goHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_password);

        // Inicialización de las vistas
        goHomeButton = findViewById(R.id.buttonGoHome);

        // Listener para el botón "Volver al inicio"
        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecoveryPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
