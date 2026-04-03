package com.leon.jobportal.service;

import com.leon.jobportal.entity.OtpToken;
import com.leon.jobportal.entity.User;
import com.leon.jobportal.exception.BadRequestException;
import com.leon.jobportal.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;

    @Transactional
    public void generateAndSendOtp(User user) {
        // Xóa OTP cũ nếu có
        otpTokenRepository.deleteByUser(user);

        // Tạo OTP 6 số
        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpToken otpToken = new OtpToken();
        otpToken.setUser(user);
        otpToken.setOtp(otp);
        otpToken.setExpiryDate(Instant.now().plusSeconds(300)); // 5 phút
        otpToken.setUsed(false);

        otpTokenRepository.save(otpToken);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Transactional
    public void verifyOtp(User user, String otp) {
        OtpToken otpToken = otpTokenRepository
                .findTopByUserAndUsedFalseOrderByExpiryDateDesc(user)
                .orElseThrow(() -> new BadRequestException("OTP not found"));

        if (otpToken.isExpired()) {
            otpTokenRepository.delete(otpToken);
            throw new BadRequestException("OTP has expired");
        }

        if (!otpToken.getOtp().equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);
    }
}