package com.codewithtaofeek.store.service;

import com.codewithtaofeek.store.dto.*;
import com.codewithtaofeek.store.entity.User;
import com.codewithtaofeek.store.repository.UserRepository;
import com.codewithtaofeek.store.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmailService emailService;

    public String signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent())
            return "Email already registered";

        String token = UUID.randomUUID().toString();
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerificationToken(token);
        userRepository.save(user);

        //emailService.sendVerificationEmail(request.getEmail(), token);
        return "Signup successful. Please check your email to verify.";
    }

    public String verify(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isEmpty()) return "Invalid token";

        User user = userOpt.get();
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return "Email verified. You can now login.";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        System.out.println(user);
        if (!user.isEnabled()) throw new RuntimeException("Email not verified");

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        String accessToken = jwtService.generateToken(user.getEmail());
        System.out.println("AccessToken: " + accessToken);
        String refreshToken = UUID.randomUUID().toString();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }

    public AuthResponse refresh(String email, String refreshToken) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (!user.getRefreshToken().equals(refreshToken))
            throw new RuntimeException("Invalid refresh token");

        String accessToken = jwtService.generateToken(email);
        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }
}
