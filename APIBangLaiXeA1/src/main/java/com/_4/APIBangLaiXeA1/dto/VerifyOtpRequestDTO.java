package com._4.APIBangLaiXeA1.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyOtpRequestDTO {
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "OTP không được để trống")
    private String otp;

    public VerifyOtpRequestDTO() {}

    public VerifyOtpRequestDTO(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}