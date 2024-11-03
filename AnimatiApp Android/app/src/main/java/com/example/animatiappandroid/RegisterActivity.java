package com.example.animatiappandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private CheckBox checkBoxTerms, checkBoxNewsletter;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = findViewById(R.id.usernameEditText);
        editTextFirstName = findViewById(R.id.firstNameEditText);
        editTextLastName = findViewById(R.id.lastNameEditText);
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        editTextConfirmPassword = findViewById(R.id.confirmPasswordEditText);
        checkBoxTerms = findViewById(R.id.termsCheckBox);
        checkBoxNewsletter = findViewById(R.id.newsletterCheckBox);
        registerButton = findViewById(R.id.registerButton);

        disableRegisterButton();

        checkBoxTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableRegisterButton();
            } else {
                disableRegisterButton();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                if (validateInput(username, firstName, lastName, email, password, confirmPassword)) {
                    new RegisterUserTask(username, firstName, lastName, email, password).execute();
                }
            }
        });
    }

    private void disableRegisterButton() {
        registerButton.setEnabled(false);
        registerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.disabled_button));
    }

    private void enableRegisterButton() {
        registerButton.setEnabled(true);
        registerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.active_button));
    }

    private boolean validateInput(String username, String firstName, String lastName, String email, String password, String confirmPassword) {
        if (username.length() < 3 || username.length() > 20) {
            showAlertDialog("Error", "El nombre de usuario debe tener entre 3 y 20 caracteres");
            return false;
        }

        if (firstName.length() < 3 || firstName.length() > 20) {
            showAlertDialog("Error", "El nombre debe tener entre 3 y 20 caracteres");
            return false;
        }

        if (lastName.length() < 3 || lastName.length() > 20) {
            showAlertDialog("Error", "El apellido debe tener entre 3 y 20 caracteres");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAlertDialog("Error", "El correo electrónico no es válido");
            return false;
        }

        if (!validatePassword(password)) {
            showAlertDialog("Error", "La contraseña debe tener al menos 6 caracteres, una letra mayúscula, una letra minúscula y un número.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlertDialog("Error", "Las contraseñas no coinciden");
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$");
        return passwordPattern.matcher(password).matches();
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private class RegisterUserTask extends AsyncTask<Void, Void, String> {

        private String username, firstName, lastName, email, password;

        public RegisterUserTask(String username, String firstName, String lastName, String email, String password) {
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://animatiapp.up.railway.app/api/registro");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", username);
                jsonParam.put("email", email);
                jsonParam.put("password", password);
                jsonParam.put("first_name", firstName);
                jsonParam.put("last_name", lastName);

                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    return "Registro completado con éxito";
                } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                    return "Error: El usuario ya existe o hay datos faltantes.";
                } else {
                    return "Error en el registro: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Error de conexión";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Registro completado con éxito")) {
                showSuccessDialog(username);
            } else {
                showAlertDialog("Error", result);
            }
        }

        private void showSuccessDialog(String username) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setTitle("Éxito")
                    .setMessage("Bienvenido a AnimatiApp " + username)
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .show();
        }
    }
}
