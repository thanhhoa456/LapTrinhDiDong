package com.example.laixea1.api;

import com.example.laixea1.dto.ApiResponseDTO;
import com.example.laixea1.dto.ForgotPasswordRequestDTO;
import com.example.laixea1.dto.LoginRequestDTO;
import com.example.laixea1.dto.ResetPasswordRequestDTO;
import com.example.laixea1.dto.UserAccountDTO;
import com.example.laixea1.dto.VerifyOtpRequestDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {

    @POST("user-accounts")
    Call<UserAccountDTO> signup(@Body UserAccountDTO userAccountDTO);

    @POST("user-accounts/login")
    Call<UserAccountDTO> login(@Body LoginRequestDTO loginRequestDTO);




    @POST("/api/user-accounts/verify-otp")
    Call<ApiResponseDTO> verifyOtp(@Body VerifyOtpRequestDTO verifyOtpRequestDTO);

    @POST("/api/user-accounts/resend-otp")
    Call<String> resendOtp(@Query("email") String email);

    @POST("user-accounts/forgot-password")
    Call<ApiResponseDTO> forgotPassword(@Body ForgotPasswordRequestDTO forgotPasswordRequestDTO);

    @POST("user-accounts/reset-password")
    Call<ApiResponseDTO> resetPassword(@Body ResetPasswordRequestDTO resetPasswordRequestDTO);
}