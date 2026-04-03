package com.leon.jobportal.repository;

import com.leon.jobportal.entity.OtpToken;
import com.leon.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByUserAndUsedFalseOrderByExpiryDateDesc(User user);
    @Transactional
    @Modifying
    void deleteByUser(User user);
}