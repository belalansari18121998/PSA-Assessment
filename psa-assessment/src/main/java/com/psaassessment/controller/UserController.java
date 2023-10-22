package com.psaassessment.controller;

import com.psaassessment.entity.User;
import com.psaassessment.payload.JWTAuthResponse;
import com.psaassessment.payload.LoginDto;
import com.psaassessment.repository.UserRepository;
import com.psaassessment.security.JwtTokenProvider;
import com.psaassessment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private RememberMeServices rememberMeServices;




    //http://localhost:8080/api/users
    @PostMapping
    public ResponseEntity<?> createRegistration(@RequestBody User user){
        if (userRepo.existsByEmail(user.getEmail())) {
            return new ResponseEntity<>("Email Already Exists", HttpStatus.BAD_REQUEST);
        }
        if (userRepo.existsByUsername(user.getUsername())) {
            return new ResponseEntity<>("Username Already Exists", HttpStatus.BAD_REQUEST);
        }
        User registerUser = userService.registerUser(user);
        return new ResponseEntity<>(registerUser, HttpStatus.CREATED);
    }
    //http://localhost:8080/api/users/verify-otp?email=18mohammadbelalansari@gmail.com&otp=387290

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        if (userService.verifyOtp(email, otp)) {
            // If OTP is valid, you can perform additional actions here, such as marking the user as verified.
            // For demonstration purposes, we'll return a success response.
            return new ResponseEntity<>("OTP Verified", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
        }
    }


    //http://localhost:8080/api/users/signin
    @PostMapping("/signin")
    public ResponseEntity<JWTAuthResponse> authenticateUser(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create a remember-me token if the "rememberMe" checkbox is selected
        if (loginDto.isRememberMe()) {
            rememberMeServices.loginSuccess(request, response, authentication);
        }

        // Get a JWT token from tokenProvider
        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JWTAuthResponse(token));
    }

}
