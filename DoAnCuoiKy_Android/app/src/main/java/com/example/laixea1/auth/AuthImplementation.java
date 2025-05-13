package com.example.laixea1.auth;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.example.laixea1.api.AuthService;
import com.example.laixea1.api.RetrofitClient;
import com.example.laixea1.dto.ApiResponseDTO;
import com.example.laixea1.dto.ForgotPasswordRequestDTO;
import com.example.laixea1.dto.LoginRequestDTO;
import com.example.laixea1.dto.ResetPasswordRequestDTO;
import com.example.laixea1.dto.UserAccountDTO;
import com.example.laixea1.dto.VerifyOtpRequestDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthImplementation {

    private Context context;
    private AuthService authService;

    public AuthImplementation(Context context) {
        this.context = context;
        this.authService = RetrofitClient.createService(AuthService.class);
    }

    // Signup functionality
    public void signup(String email, String password, String confirmPassword, AuthCallback callback) {
        if (!password.equals(confirmPassword)) {
            callback.onFailure("Passwords do not match");
            return;
        }

        UserAccountDTO userAccountDTO = new UserAccountDTO();
        userAccountDTO.setEmail(email);
        userAccountDTO.setPassword(password);
        userAccountDTO.setVerified(false);

        Call<UserAccountDTO> call = authService.signup(userAccountDTO);
        call.enqueue(new Callback<UserAccountDTO>() {
            @Override
            public void onResponse(Call<UserAccountDTO> call, Response<UserAccountDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess("Registration successful. Please verify your email.");
                } else {
                    callback.onFailure(response.message() != null ? response.message() : "Signup failed");
                }
            }

            @Override
            public void onFailure(Call<UserAccountDTO> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    // Login functionality
    public void login(String email, String password, AuthCallback callback) {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail(email);
        loginRequestDTO.setPassword(password);

        Call<UserAccountDTO> call = authService.login(loginRequestDTO);
        call.enqueue(new Callback<UserAccountDTO>() {
            @Override
            public void onResponse(Call<UserAccountDTO> call, Response<UserAccountDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess("Đăng nhập thành công");
                } else {
                    String errorMsg = response.message() != null ? response.message() : "Đăng nhập thất bại";
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<UserAccountDTO> call, Throwable t) {
                Log.e("AuthImplementation", "Login error: " + t.getMessage(), t);
                callback.onFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void forgotPassword(String email, AuthCallback callback) {
        ForgotPasswordRequestDTO forgotPasswordRequestDTO = new ForgotPasswordRequestDTO();
        forgotPasswordRequestDTO.setEmail(email);

        Call<ApiResponseDTO> call = authService.forgotPassword(forgotPasswordRequestDTO);
        call.enqueue(new Callback<ApiResponseDTO>() {
            @Override
            public void onResponse(Call<ApiResponseDTO> call, Response<ApiResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMessage());
                } else {
                    callback.onFailure(response.message() != null ? response.message() : "Failed to send OTP");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDTO> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void resetPassword(String email, String otp, String newPassword, String confirmPassword, AuthCallback callback) {
        if (!newPassword.equals(confirmPassword)) {
            callback.onFailure("Passwords do not match");
            return;
        }

        ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO();
        resetPasswordRequestDTO.setEmail(email);
        resetPasswordRequestDTO.setOtp(otp);
        resetPasswordRequestDTO.setNewPassword(newPassword);

        Call<ApiResponseDTO> call = authService.resetPassword(resetPasswordRequestDTO);
        call.enqueue(new Callback<ApiResponseDTO>() {
            @Override
            public void onResponse(Call<ApiResponseDTO> call, Response<ApiResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMessage());
                } else {
                    callback.onFailure(response.message() != null ? response.message() : "Password reset failed");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDTO> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    // Verify OTP
    public void verifyOtp(String email, String otp, AuthCallback callback) {
        VerifyOtpRequestDTO verifyOtpRequestDTO = new VerifyOtpRequestDTO();
        verifyOtpRequestDTO.setEmail(email);
        verifyOtpRequestDTO.setOtp(otp);

        Call<ApiResponseDTO> call = authService.verifyOtp(verifyOtpRequestDTO);
        call.enqueue(new Callback<ApiResponseDTO>() {
            @Override
            public void onResponse(Call<ApiResponseDTO> call, Response<ApiResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMessage());
                } else {
                    callback.onFailure(response.message() != null ? response.message() : "Mã OTP không hợp lệ");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDTO> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    // Resend OTP
    public void resendOtp(String email, AuthCallback callback) {
        Call<String> call = authService.resendOtp(email);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(response.message() != null ? response.message() : "Không thể gửi lại OTP");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public interface AuthCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }
}