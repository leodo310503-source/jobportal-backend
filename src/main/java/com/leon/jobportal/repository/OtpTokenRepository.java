package com.leon.jobportal.repository;

import com.leon.jobportal.entity.OtpToken;
import com.leon.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByUserAndUsedFalseOrderByExpiryDateDesc(User user);
    void deleteByUser(User user);
}