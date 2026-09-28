package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Map<String, Object> login(Map<String, Object> request) {

        if (request == null) {
            throw new RuntimeException("Request body is required");
        }

        String email = request.get("email") != null
                ? request.get("email").toString().trim()
                : null;

        String password = request.get("password") != null
                ? request.get("password").toString()
                : null;

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (password == null || password.isBlank()) {
            throw new RuntimeException("Password is required");
        }

        UserAccount user = userAccountRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new RuntimeException("User account is inactive");
        }

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new RuntimeException("Invalid credentials");
        }

        user.setLastLogin(LocalDateTime.now());

        userAccountRepository.save(user);


        String token = jwtService.generateToken(user);

        Map<String, Object> userData = new HashMap<>();

        userData.put("id", user.getId());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("gender", user.getGender());
        userData.put("isActive", user.getIsActive());
        userData.put("createdAt", user.getCreatedAt());
        userData.put("updatedAt", user.getUpdatedAt());
        userData.put("lastLogin", user.getLastLogin());

        Map<String, Object> data = new HashMap<>();

        data.put("token", token);
        data.put("user", userData);

        Map<String, Object> response = new HashMap<>();

        response.put("data", data);
        response.put("message", "Login successful");
        response.put("success", true);

        return response;
    }
}