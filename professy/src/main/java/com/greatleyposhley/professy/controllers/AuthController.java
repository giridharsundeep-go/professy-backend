package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.services.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, Object> request) {

        try {

            Map<String, Object> response =
                    authService.login(request);

            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {

            Map<String, Object> error =
                    new HashMap<>();

            error.put("message", ex.getMessage());

            if ("User not found".equals(ex.getMessage())) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(error);
            }

            if ("Invalid credentials".equals(ex.getMessage())) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(error);
            }

            if ("User account is inactive".equals(ex.getMessage())) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(error);
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error);
        }
    }


}