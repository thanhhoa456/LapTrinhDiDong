package com._4.APIBangLaiXeA1.repo;

import com._4.APIBangLaiXeA1.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpVerificationRepo extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByEmailAndOtpAndType(String email, String otp, OtpVerification.OtpType type);
    void deleteByEmailAndType(String email, OtpVerification.OtpType type);
    void deleteAllByExpiryDateBefore(LocalDateTime time);

}