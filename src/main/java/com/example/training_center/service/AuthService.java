package com.example.training_center.service;

import com.example.training_center.dto.*;
import com.example.training_center.model.User;
import com.example.training_center.repository.UserRepository;
import com.example.training_center.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder; // ✅ Add this

    private final Map<String, String> otpCache = new ConcurrentHashMap<>();
    private final Map<String, String> resetTokenCache = new ConcurrentHashMap<>();

    public ResponseEntity<?> login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String otp = String.format("%06d", new Random().nextInt(999999));
        otpCache.put(user.getEmail(), otp);
        emailService.sendOtp(user.getEmail(), otp);

        return ResponseEntity.ok("OTP sent to email");
    }

    public ResponseEntity<?> verifyOtp(OtpRequest request) {
        String storedOtp = otpCache.get(request.getEmail());

        if (storedOtp != null && storedOtp.equals(request.getOtp())) {
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            user.setVerified(true);
            userRepository.save(user);

            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(Map.of("token", token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
    }
    // Add near top

    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String otp = String.format("%06d", new Random().nextInt(999999));
        resetTokenCache.put(user.getEmail(), otp);

        emailService.sendOtp(user.getEmail(), otp);
        return ResponseEntity.ok("OTP sent for password reset.");
    }

    public ResponseEntity<?> resetPassword(ResetPasswordRequest request) {
        String validOtp = resetTokenCache.get(request.getEmail());

        if (validOtp != null && validOtp.equals(request.getOtp())) {
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            resetTokenCache.remove(request.getEmail());
            userRepository.save(user);
            return ResponseEntity.ok("Password reset successful.");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP.");
    }
    public ResponseEntity<?> register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase()) // Enforce role capitalization
                .isVerified(false)
                .build();

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }


}
