package com.example.laixea1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.laixea1.R;
import com.example.laixea1.auth.AuthImplementation;
import com.google.android.material.textfield.TextInputEditText;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText otpEditText, newPasswordEditText, confirmPasswordEditText;
    private Button resetButton;
    private AuthImplementation authImpl;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        authImpl = new AuthImplementation(this);

        // Lấy email từ Intent
        email = getIntent().getStringExtra("EMAIL");

        otpEditText = findViewById(R.id.otp);
        newPasswordEditText = findViewById(R.id.new_password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        resetButton = findViewById(R.id.reset_button);

        resetButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ các trường", Toast.LENGTH_SHORT).show();
                return;
            }

            authImpl.resetPassword(email, otp, newPassword, confirmPassword, new AuthImplementation.AuthCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}