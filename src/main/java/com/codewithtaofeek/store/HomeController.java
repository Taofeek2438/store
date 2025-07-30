package com.codewithtaofeek.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        try {
            Authentication auth = authenticationManager.authenticate(token);
            System.out.println(request.getPassword());
            return "Login successful for user: " + auth.getName();
        } catch (AuthenticationException e) {
            return "Login failed: " + e.getMessage();
        }
    }
}
