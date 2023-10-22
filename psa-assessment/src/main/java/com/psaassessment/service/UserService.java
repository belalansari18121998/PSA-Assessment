package com.psaassessment.service;

import com.psaassessment.entity.User;

public interface UserService {
    User registerUser(User user);
    boolean verifyOtp(String email, String otp);
}
