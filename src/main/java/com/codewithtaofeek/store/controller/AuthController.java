package com.codewithtaofeek.store.controller;

import com.codewithtaofeek.store.dto.*;
import com.codewithtaofeek.store.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
        return authService.verify(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        System.out.println("Loggin In");
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam String email, @RequestParam String refreshToken) {
        return authService.refresh(email, refreshToken);
    }
}