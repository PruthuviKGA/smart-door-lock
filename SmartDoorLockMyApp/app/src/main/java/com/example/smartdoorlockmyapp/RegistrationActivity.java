package com.example.smartdoorlockmyapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class RegistrationActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText;
    private Button fingerprintButton, registerButton, cancelButton;
    private int fingerprintId = -1; // Default no fingerprint

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.editTextEmail);
        fingerprintButton = findViewById(R.id.buttonFingerprint);
        registerButton = findViewById(R.id.buttonRegister);
        cancelButton = findViewById(R.id.buttonCancel);

        fingerprintButton.setOnClickListener(v -> captureFingerprint());
        registerButton.setOnClickListener(v -> registerUser());
        cancelButton.setOnClickListener(v -> finish()); // Go back
    }

    private void captureFingerprint() {
        Random random = new Random();
        fingerprintId = random.nextInt(1000); // Simulate fingerprint ID
        new AlertDialog.Builder(this)
                .setTitle("Fingerprint Captured")
                .setMessage("Fingerprint ID: " + fingerprintId)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        System.out.println(username);

        if (username.isEmpty() || email.isEmpty() || fingerprintId == -1) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.192.105:8070/user/createuser");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("username", username);
                json.put("email", email);
                json.put("fingerprintId", fingerprintId);


                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                runOnUiThread(() -> {
                    if (responseCode == 201) {
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
