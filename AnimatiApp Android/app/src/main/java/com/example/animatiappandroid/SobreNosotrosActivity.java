package com.example.animatiappandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SobreNosotrosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre_nosotros);

        Button volverAtrasButton = findViewById(R.id.volverAtrasButton);
        volverAtrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverAtras(v);
            }
        });
    }

    public void volverAtras(View view) {
        finish();
    }
}
