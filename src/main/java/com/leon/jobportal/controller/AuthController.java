package com.leon.jobportal.controller;

import com.leon.jobportal.dto.AuthResponse;
import com.leon.jobportal.dto.LoginRequest;
import com.leon.jobportal.dto.RegisterRequest;
import com.leon.jobportal.entity.RefreshToken;
import com.leon.jobportal.entity.User;
import com.leon.jobportal.exception.ResourceNotFoundException;
import com.leon.jobportal.repository.UserRepository;
import com.leon.jobportal.service.AuthService;
import com.leon.jobportal.service.OtpService;
import com.leon.jobportal.service.RefreshTokenService;
import com.leon.jobportal.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String email,
                                              @RequestParam String otp) {
        authService.verifyEmail(email, otp);
        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));
        otpService.generateAndSendOtp(user);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
        RefreshToken token = refreshTokenService.verifyToken(refreshToken);
        User user = token.getUser();
        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(
                newAccessToken,
                token.getToken(),
                user.getEmail(),
                user.getRole().name()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String refreshToken) {
        RefreshToken token = refreshTokenService.verifyToken(refreshToken);
        refreshTokenService.deleteByUser(token.getUser());
        return ResponseEntity.ok("Logged out successfully");
    }
}