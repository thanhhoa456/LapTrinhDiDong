package com.example.laixea1.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laixea1.R;
import com.example.laixea1.auth.AuthImplementation;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton, guestButton;
    private CheckBox rememberMeCheckBox;
    private TextView signupLink, forgotPasswordLink;
    private AuthImplementation authImpl;
    private SharedPreferences appPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authImpl = new AuthImplementation(this);
        appPrefs = getSharedPreferences("App_Settings", MODE_PRIVATE);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        guestButton = findViewById(R.id.guest_button);
        rememberMeCheckBox = findViewById(R.id.remember_me);
        signupLink = findViewById(R.id.signup_link);
        forgotPasswordLink = findViewById(R.id.forgot_password_link);

        // Kiểm tra Remember Me
        if (appPrefs.getBoolean("remember_me", false)) {
            String savedEmail = appPrefs.getString("saved_email", "");
            if (!savedEmail.isEmpty()) {
                emailEditText.setText(savedEmail);
                rememberMeCheckBox.setChecked(true);
            }
        }

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            authImpl.login(email, password, new AuthImplementation.AuthCallback() {
                @Override
                public void onSuccess(String message) {
                    // Lưu current_user và Remember Me
                    SharedPreferences.Editor editor = appPrefs.edit();
                    editor.putString("current_user", email);
                    if (rememberMeCheckBox.isChecked()) {
                        editor.putBoolean("remember_me", true);
                        editor.putString("saved_email", email);
                    } else {
                        editor.putBoolean("remember_me", false);
                        editor.remove("saved_email");
                    }
                    editor.apply();

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        guestButton.setOnClickListener(v -> {
            // Lưu "Guest" làm current_user, không lưu Remember Me
            SharedPreferences.Editor editor = appPrefs.edit();
            editor.putString("current_user", "Guest");
            editor.putBoolean("remember_me", false);
            editor.remove("saved_email");
            editor.apply();

            Toast.makeText(this, "Continuing as Guest", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

        signupLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        forgotPasswordLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }
}