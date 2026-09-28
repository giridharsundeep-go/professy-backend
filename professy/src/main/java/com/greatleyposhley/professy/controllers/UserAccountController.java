package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.services.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserAccountController {

    private final UserAccountService userService;

    public UserAccountController(UserAccountService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers() {

        try {

            List<UserAccount> users = userService.getAllUsers();

            Map<String, Object> response = new HashMap<>();

            response.put("data", users);
            response.put("success", true);
            response.put("message", "Users fetched successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Map<String, Object> response = new HashMap<>();

            response.put("success", false);
            response.put("message", "Failed to fetch users");
            response.put("error", e.getMessage());

            return ResponseEntity
                    .internalServerError()
                    .body(response);
        }
    }

}
