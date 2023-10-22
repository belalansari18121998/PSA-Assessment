package com.psaassessment.controller;

import com.psaassessment.service.impl.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ForgotPasswordController {
    @Autowired
    private ForgotPasswordService service;

    //http://localhost:8080/reset-password?token=c03b67ce-ab1d-499f-915a-2b1ee731cb822d8163cf-f407-4bc6-bccf-41e0dc940d2f

    @PostMapping("/forgot-password")
    public String forgotPass(@RequestParam String email){
        String response = service.forgotPass(email);

        if(!response.startsWith("Invalid")){
            response= "http://localhost:8080/api/reset-password?token=" + response;
        }
        return response;
    }

    @PutMapping("/reset-password")
    public String resetPass(@RequestParam String token, @RequestParam String password){
        if (token == null || password == null) {
            return "Missing parameters"; // Handle missing parameters
        }

        return service.resetPassword(token, password);
    }

}

