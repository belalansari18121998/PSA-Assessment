package com.psaassessment.repository;

import com.psaassessment.entity.ForgotPassword;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordRepo extends JpaRepository<ForgotPassword, Long> {
    ForgotPassword findByToken(String token);
}

