package com.psaassessment.service.impl;

import com.psaassessment.entity.User;
import com.psaassessment.repository.UserRepository;
import com.psaassessment.service.UserService;
import com.psaassessment.utils.EmailService;
import com.psaassessment.utils.OtpService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, String> otpStorage = new HashMap<>();

    @Override
    public User registerUser(User user) {
        String randomId = UUID.randomUUID().toString();
        user.setId(randomId);
        String otp = otpService.generateOTP();
        emailService.sendVerificationEmail(user.getEmail(), otp);
        otpStorage.put(user.getEmail(), otp);

        if (verifyOtp(user.getEmail(), otp) && user.getPassword().equals(user.getReEnterPassword())) {
            // Encode the password before saving it in the database
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userRepo.save(user);
        }

        return user;
    }

    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        return storedOtp != null && storedOtp.equals(otp);
    }

}
