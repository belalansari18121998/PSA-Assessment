package com.psaassessment.service.impl;

import com.psaassessment.entity.ForgotPassword;
import com.psaassessment.entity.User;
import com.psaassessment.repository.ForgotPasswordRepo;
import com.psaassessment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForgotPasswordService {
    private static final long EXPIRE_TOKEN = 30;

    @Autowired
    private ForgotPasswordRepo forgotPasswordRepo;

    @Autowired
    private UserRepository userRepository;

    public  String forgotPass(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (!userOptional.isPresent()) {
            return "Invalid email id.";
        }

        User user = userOptional.get();

        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setUserId(user.getId());

        // Debugging: Print a message to confirm that generateToken is called
        System.out.println("Calling generateToken...");
        forgotPassword.setToken(generateToken());

        forgotPassword.setTokenCreationDate(LocalDateTime.now());

        // Debugging: Print the generated token
        System.out.println("Generated token: " + forgotPassword.getToken());

        forgotPasswordRepo.save(forgotPassword);

        return forgotPassword.getToken();
    }

    public String resetPassword(String token, String password) {
        // Find the ForgotPassword entity by token
        Optional<ForgotPassword> forgotPasswordOptional = Optional.ofNullable(forgotPasswordRepo.findByToken(token));

        if (!forgotPasswordOptional.isPresent()) {
            return "Invalid token";
        }

        ForgotPassword forgotPassword = forgotPasswordOptional.get();

        if (forgotPassword.getUserId() == null) {
            return "User not found for the provided token";
        }

        // Load the associated User entity based on the userId stored in ForgotPassword
        User user = userRepository.findById(forgotPassword.getUserId()).orElse(null);

        if (user == null) {
            return "User not found for the provided token";
        }

        // Set the new password for the user after hashing
        String hashedPassword = hashPassword(password);
        user.setPassword(hashedPassword);

//        // Clear the token and tokenCreationDate from the user's ForgotPassword
        forgotPassword.setToken(null);
        forgotPassword.setTokenCreationDate(null);

        // Save the changes in a transaction
        userRepository.save(user);
        forgotPasswordRepo.save(forgotPassword);

        // Log the password reset event

        return "Your password was successfully updated.";
    }
    private String hashPassword(String password) {
        // Create an instance of the BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Hash the password
        String hashedPassword = passwordEncoder.encode(password);

        return hashedPassword;
    }

    private String generateToken() {
        StringBuilder token = new StringBuilder();
        return token.append(UUID.randomUUID().toString())
                .append(UUID.randomUUID().toString()).toString();
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);
        return diff.toMinutes() >= EXPIRE_TOKEN;
    }
}
