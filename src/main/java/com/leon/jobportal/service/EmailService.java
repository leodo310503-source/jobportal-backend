package com.leon.jobportal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Email Verification - Job Portal");
        message.setText("Your OTP code is: " + otp + "\n\nThis code will expire in 5 minutes.\n\nIf you did not request this, please ignore this email.");

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset - Job Portal");
        message.setText("Your password reset OTP code is: " + otp + "\n\nThis code will expire in 5 minutes.\n\nIf you did not request this, please ignore this email.");

        mailSender.send(message);
    }
}