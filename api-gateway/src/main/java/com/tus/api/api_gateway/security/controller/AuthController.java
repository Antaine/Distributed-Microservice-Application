package com.tus.api.api_gateway.security.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tus.api.api_gateway.security.JwtUtil;

@RestController
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            throw new RuntimeException("Username and password required");
        }

        String role;

        // demo credentials
        if ("admin".equals(username) && "admin".equals(password)) {
            role = "ADMIN";
        } else if ("john".equals(username) && "password".equals(password)) {
            role = "USER";
        } else {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(username, role);
    }
}