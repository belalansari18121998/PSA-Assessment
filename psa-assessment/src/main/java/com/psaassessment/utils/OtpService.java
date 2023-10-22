package com.psaassessment.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Service
public class OtpService {

    private final Random random;

    public OtpService(Random random) {
        this.random = random;
    }

    public String generateOTP() {
        // Generate a random 6-digit OTP
        int otp = random.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }
}

