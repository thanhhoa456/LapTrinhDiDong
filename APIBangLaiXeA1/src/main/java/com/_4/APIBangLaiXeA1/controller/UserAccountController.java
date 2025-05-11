package com._4.APIBangLaiXeA1.controller;

import com._4.APIBangLaiXeA1.dto.*;
import com._4.APIBangLaiXeA1.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-accounts")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody UserAccountDTO userAccountDTO) {
        try {
            UserAccountDTO savedUser = userAccountService.registerUser(userAccountDTO);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Lỗi hệ thống: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseDTO> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO verifyOtpRequestDTO) {
        try {
            userAccountService.verifyOtp(verifyOtpRequestDTO);
            return ResponseEntity.ok(new ApiResponseDTO("Tài khoản đã được kích hoạt"));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            UserAccountDTO user = userAccountService.login(loginRequestDTO);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        try {
            userAccountService.forgotPassword(forgotPasswordRequestDTO);
            return ResponseEntity.ok(new ApiResponseDTO("Email OTP đã được gửi"));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponseDTO("Không thể gửi email: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        try {
            userAccountService.resetPassword(resetPasswordRequestDTO);
            return ResponseEntity.ok(new ApiResponseDTO("Mật khẩu đã được đặt lại"));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestBody EmailRequestDTO emailRequestDTO) {
        userAccountService.resendVerificationOtp(emailRequestDTO.getEmail());
        return ResponseEntity.ok("OTP đã được gửi lại.");
    }

}