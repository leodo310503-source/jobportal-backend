package com.leon.jobportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "otp_tokens")
@Data
@NoArgsConstructor
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private Instant expiryDate;

    private boolean used = false;

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}