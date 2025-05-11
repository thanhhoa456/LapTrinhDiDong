package com._4.APIBangLaiXeA1.service;

import com._4.APIBangLaiXeA1.dto.UserAccountDTO;
import com._4.APIBangLaiXeA1.dto.LoginRequestDTO;
import com._4.APIBangLaiXeA1.dto.ForgotPasswordRequestDTO;
import com._4.APIBangLaiXeA1.dto.VerifyOtpRequestDTO;
import com._4.APIBangLaiXeA1.dto.ResetPasswordRequestDTO;
import com._4.APIBangLaiXeA1.entity.OtpVerification;
import com._4.APIBangLaiXeA1.entity.UserAccount;
import com._4.APIBangLaiXeA1.repo.OtpVerificationRepo;
import com._4.APIBangLaiXeA1.repo.UserAccountRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepo userAccountRepository;

    @Autowired
    private OtpVerificationRepo otpVerificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public UserAccountDTO registerUser(UserAccountDTO userAccountDTO) {
        if (userAccountRepository.existsByEmail(userAccountDTO.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(userAccountDTO.getEmail());
        userAccount.setPassword(passwordEncoder.encode(userAccountDTO.getPassword()));
        userAccount.setVerified(false);

        UserAccount savedUser = userAccountRepository.save(userAccount);

        String otp = generateOtp();
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setOtp(otp);
        otpVerification.setEmail(savedUser.getEmail());
        otpVerification.setType(OtpVerification.OtpType.VERIFICATION);
        otpVerification.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        otpVerificationRepository.save(otpVerification);

        try {
            sendOtpEmail(savedUser.getEmail(), otp, "Xác minh tài khoản");
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email OTP", e);
        }

        return new UserAccountDTO(savedUser.getId(), savedUser.getEmail(), null, savedUser.isVerified());
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendOtpEmail(String email, String otp, String subject) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(
                "<h3>" + subject + "</h3>" +
                        "<p>Mã OTP của bạn là: <strong>" + otp + "</strong></p>" +
                        "<p>Vui lòng nhập mã này trong ứng dụng để tiếp tục.</p>" +
                        "<p>Mã OTP này sẽ hết hạn sau 5 phút.</p>",
                true
        );
        mailSender.send(message);
    }

    @Transactional
    public void verifyOtp(VerifyOtpRequestDTO verifyOtpRequestDTO) {
        OtpVerification otpVerification = otpVerificationRepository
                .findByEmailAndOtpAndType(verifyOtpRequestDTO.getEmail(), verifyOtpRequestDTO.getOtp(), OtpVerification.OtpType.VERIFICATION)
                .orElseThrow(() -> new IllegalArgumentException("OTP không hợp lệ"));

        if (otpVerification.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP đã hết hạn");
        }

        UserAccount userAccount = userAccountRepository.findByEmail(verifyOtpRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
        userAccount.setVerified(true);
        userAccountRepository.save(userAccount);
        otpVerificationRepository.delete(otpVerification);
    }

    public UserAccountDTO login(LoginRequestDTO loginRequestDTO) {
        UserAccount userAccount = userAccountRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng"));
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), userAccount.getPassword())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");
        }
        if (!userAccount.isVerified()) {
            throw new IllegalArgumentException("Tài khoản chưa được xác minh");
        }
        return new UserAccountDTO(userAccount.getId(), userAccount.getEmail(), null, userAccount.isVerified());
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        UserAccount userAccount = userAccountRepository.findByEmail(forgotPasswordRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));

        // Xóa OTP cũ nếu tồn tại
        otpVerificationRepository.deleteByEmailAndType(userAccount.getEmail(), OtpVerification.OtpType.RESET_PASSWORD);

        String otp = generateOtp();
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setOtp(otp);
        otpVerification.setEmail(userAccount.getEmail());
        otpVerification.setType(OtpVerification.OtpType.RESET_PASSWORD);
        otpVerification.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        otpVerificationRepository.save(otpVerification);

        try {
            sendOtpEmail(userAccount.getEmail(), otp, "Đặt lại mật khẩu");
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email OTP", e);
        }
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // mỗi 5 phút
    @Transactional
    public void deleteExpiredOtps() {
        otpVerificationRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        OtpVerification otpVerification = otpVerificationRepository
                .findByEmailAndOtpAndType(resetPasswordRequestDTO.getEmail(), resetPasswordRequestDTO.getOtp(), OtpVerification.OtpType.RESET_PASSWORD)
                .orElseThrow(() -> new IllegalArgumentException("OTP không hợp lệ"));

        if (otpVerification.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP đã hết hạn");
        }

        UserAccount userAccount = userAccountRepository.findByEmail(resetPasswordRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
        userAccount.setPassword(passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword()));
        userAccountRepository.save(userAccount);
        otpVerificationRepository.delete(otpVerification);
    }

    @Transactional
    public void resendVerificationOtp(String email) {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));

        if (userAccount.isVerified()) {
            throw new IllegalStateException("Tài khoản đã xác minh");
        }

        // Xóa OTP cũ nếu có
        otpVerificationRepository.deleteByEmailAndType(email, OtpVerification.OtpType.VERIFICATION);

        // Tạo OTP mới
        String otp = generateOtp();
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setOtp(otp);
        otpVerification.setEmail(email);
        otpVerification.setType(OtpVerification.OtpType.VERIFICATION);
        otpVerification.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        otpVerificationRepository.save(otpVerification);

        try {
            sendOtpEmail(email, otp, "Xác minh tài khoản");
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi lại OTP", e);
        }
    }
}