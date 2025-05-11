package com.example.laixea1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.laixea1.R;
import com.example.laixea1.auth.AuthImplementation;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signupButton;
    private TextView loginLink;
    private AuthImplementation authImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        authImpl = new AuthImplementation(this);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        signupButton = findViewById(R.id.signup_button);
        loginLink = findViewById(R.id.login_link);

        signupButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            authImpl.signup(email, password, confirmPassword, new AuthImplementation.AuthCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, VerifyOtpActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(SignupActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }
}