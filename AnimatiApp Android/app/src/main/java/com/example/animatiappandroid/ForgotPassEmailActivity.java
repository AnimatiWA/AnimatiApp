package com.example.animatiappandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class ForgotPassEmailActivity extends AppCompatActivity {
    private Button sendButton;
    private EditText newPasswordEditText, repeatPasswordEditText, codeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_email);


        sendButton = findViewById(R.id.buttonSend);
        codeEditText = findViewById(R.id.codeEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);


        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {

                String token = uri.getLastPathSegment();

                SharedPreferences sharedPreferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", token);
                editor.apply();
            }
        }


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPassword = newPasswordEditText.getText().toString();
                String repeatPassword = repeatPasswordEditText.getText().toString();


                if (validatePasswords(newPassword, repeatPassword)) {

                    new RecoverPasswordTask(newPassword).execute();
                }
            }
        });
    }

    private boolean validatePasswords(String newPassword, String repeatPassword) {

        if (!newPassword.equals(repeatPassword)) {
            showAlertDialog("Error", "Las contraseñas no coinciden.");
            return false;
        }


        if (!validatePassword(newPassword)) {
            showAlertDialog("Error", "La contraseña debe tener al menos 6 caracteres, una letra mayúscula, una letra minúscula y un número.");
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {

        return password.length() >= 6;
    }

    private void showAlertDialog(String title, String message) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void showSuccessDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Éxito")
                .setMessage("Contraseña actualizada exitosamente.")

                .setPositiveButton("Aceptar", null)
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

                SharedPreferences sharedPreferences = getSharedPreferences("AnimatiPreferencias", Context.MODE_PRIVATE);
                String codigo = codeEditText.getText().toString().trim();


                URL url = new URL("https://animatiapp.up.railway.app/api/resetPassword");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);


                JSONObject jsonParam = new JSONObject();
                jsonParam.put("codigo", codigo);
                jsonParam.put("password", newPassword);
                jsonParam.put("password2", newPassword);


                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();


                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Contraseña actualizada con éxito";
                } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {

                    return "Código incorrecto. Por favor, verifica el código de verificación.";
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
                Intent intent = new Intent(ForgotPassEmailActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {

                if (result.contains("Código incorrecto")) {
                    Toast.makeText(ForgotPassEmailActivity.this, "El código de verificación es incorrecto. Por favor, inténtalo de nuevo.", Toast.LENGTH_LONG).show();
                } else {
                    showAlertDialog("Error", result);
                }
            }
        }
    }
}
