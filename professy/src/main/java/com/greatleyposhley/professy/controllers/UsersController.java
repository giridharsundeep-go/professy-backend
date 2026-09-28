package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.Users;
import com.greatleyposhley.professy.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    /*
     * GET ALL USERS
     *
     * Angular:
     * GET /api/user
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {

        try {

            List<Users> users =
                    usersService.getAllUsers();

            Map<String, Object> response =
                    new HashMap<>();

            response.put("data", users);
            response.put("success", true);
            response.put(
                    "message",
                    "Users fetched successfully"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Map<String, Object> response =
                    new HashMap<>();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    /*
     * GET USER BY ID
     *
     * GET /api/user/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(
            @PathVariable Long id) {

        try {

            Users user =
                    usersService.getUserById(id);

            Map<String, Object> response =
                    new HashMap<>();

            response.put("data", user);
            response.put("success", true);
            response.put(
                    "message",
                    "User fetched successfully"
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, Object> response =
                    new HashMap<>();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }
    }

    /*
     * GET USER BY EMAIL
     *
     * GET /api/users/by-email?email=...
     */
    @GetMapping("/users/by-email")
    public ResponseEntity<Map<String, Object>> getUserByEmail(
            @RequestParam String email) {

        try {

            Users user =
                    usersService.getUserByEmail(email);

            Map<String, Object> response =
                    new HashMap<>();

            response.put("data", user);
            response.put("success", true);
            response.put(
                    "message",
                    "User fetched successfully"
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, Object> response =
                    new HashMap<>();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }
    }

    /*
     * CHECK EMAIL
     *
     * GET /api/users/exists?email=...
     */
    @GetMapping("/users/exists")
    public ResponseEntity<Map<String, Object>> checkEmailExists(
            @RequestParam String email) {

        boolean exists =
                usersService.existsByEmail(email);

        Map<String, Object> response =
                new HashMap<>();

        response.put("data", exists);
        response.put("success", true);
        response.put(
                "message",
                "Email existence checked successfully"
        );

        return ResponseEntity.ok(response);
    }

    /*
     * CREATE USER
     *
     * POST /api/user/create
     *
     * The authenticated UserAccount is obtained
     * from JWT/SecurityContext.
     */
    @PostMapping("/user/create")
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestBody Users user,
            Authentication authentication) {

        try {

            if (authentication == null ||
                    !authentication.isAuthenticated()) {

                Map<String, Object> response =
                        new HashMap<>();

                response.put("success", false);
                response.put(
                        "message",
                        "Authentication required"
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            /*
             * This is the email from JWT subject.
             */
            String authenticatedUserEmail =
                    authentication.getName();

            Users createdUser =
                    usersService.createUser(
                            user,
                            authenticatedUserEmail
                    );

            Map<String, Object> response =
                    new HashMap<>();

            response.put("data", createdUser);
            response.put("success", true);
            response.put(
                    "message",
                    "User created successfully"
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException e) {

            Map<String, Object> response =
                    new HashMap<>();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    /*
     * UPDATE USER
     *
     * PUT /api/user/{id}
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestBody Users user) {

        try {

            Users updatedUser =
                    usersService.updateUser(
                            id,
                            user
                    );

            Map<String, Object> response =
                    new HashMap<>();

            response.put("data", updatedUser);
            response.put("success", true);
            response.put(
                    "message",
                    "User updated successfully"
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, Object> response =
                    new HashMap<>();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    /*
     * DELETE USER
     *
     * DELETE /api/user/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @PathVariable Long id) {

        try {

            usersService.deleteUser(id);

            Map<String, Object> response =
                    new HashMap<>();

            response.put("success", true);
            response.put(
                    "message",
                    "User deleted successfully"
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, Object> response =
                    new HashMap<>();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }
}