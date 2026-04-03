package com.leon.jobportal.service;

import com.leon.jobportal.dto.AuthResponse;
import com.leon.jobportal.dto.LoginRequest;
import com.leon.jobportal.dto.RegisterRequest;
import com.leon.jobportal.dto.ResetPasswordRequest;
import com.leon.jobportal.entity.RefreshToken;
import com.leon.jobportal.entity.Role;
import com.leon.jobportal.entity.User;
import com.leon.jobportal.exception.BadRequestException;
import com.leon.jobportal.exception.ConflictException;
import com.leon.jobportal.exception.ResourceNotFoundException;
import com.leon.jobportal.repository.UserRepository;
import com.leon.jobportal.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole()));
        user.setVerified(false);
        userRepository.save(user);

        // Gửi OTP sau khi đăng ký
        otpService.generateAndSendOtp(user);

        return new AuthResponse(null, null, user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        // Kiểm tra tài khoản đã verify chưa
        if (!user.getVerified()) {
            throw new BadRequestException("Please verify your email before logging in");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken(), user.getEmail(), user.getRole().name());
    }

    public void verifyEmail(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        if (user.getVerified()) {
            throw new BadRequestException("Email already verified");
        }

        otpService.verifyOtp(user, otp);

        user.setVerified(true);
        userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        otpService.generateAndSendOtp(user);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        otpService.verifyOtp(user, request.getOtp());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setVerified(true); // thêm dòng này
        userRepository.save(user);
    }
}