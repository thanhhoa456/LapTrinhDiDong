package com.example.laixea1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laixea1.R;
import com.example.laixea1.auth.AuthImplementation;
import com.google.android.material.textfield.TextInputEditText;

public class VerifyOtpActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, otpEditText;
    private Button verifyButton;
    private TextView resendOtpTextView;
    private AuthImplementation authImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        authImpl = new AuthImplementation(this);

        // Khởi tạo các view
        emailEditText = findViewById(R.id.email);
        otpEditText = findViewById(R.id.otp);
        verifyButton = findViewById(R.id.verify_button);
        resendOtpTextView = findViewById(R.id.resend_otp);

        // Sự kiện click nút Xác minh OTP
        verifyButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String otp = otpEditText.getText().toString().trim();

            // Kiểm tra đầu vào
            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Vui lòng nhập email");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Email không hợp lệ");
                return;
            }
            if (TextUtils.isEmpty(otp)) {
                otpEditText.setError("Vui lòng nhập mã OTP");
                return;
            }

            // Gọi API xác minh OTP
            authImpl.verifyOtp(email, otp, new AuthImplementation.AuthCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(VerifyOtpActivity.this, message, Toast.LENGTH_SHORT).show();
                    // Chuyển đến màn hình đăng nhập sau khi xác minh tài khoản
                    Intent intent = new Intent(VerifyOtpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(VerifyOtpActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        });

        // Sự kiện click Gửi lại mã OTP
        resendOtpTextView.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            // Kiểm tra email
            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Vui lòng nhập email");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Email không hợp lệ");
                return;
            }

            // Gọi API gửi lại OTP
            authImpl.resendOtp(email, new AuthImplementation.AuthCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(VerifyOtpActivity.this, "Mã OTP đã được gửi lại", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(VerifyOtpActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}