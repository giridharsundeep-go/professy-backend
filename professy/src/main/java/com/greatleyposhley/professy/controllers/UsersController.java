package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.entities.Users;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import com.greatleyposhley.professy.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsersController {
    private final UsersService usersService;
    private final UserAccountRepository userAccountRepository;

    public UsersController(UsersService usersService, UserAccountRepository userAccountRepository) {
        this.usersService = usersService;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(Authentication auth) {
        try { return success(usersService.getAllUsers(getOrgId(auth)), "Users fetched successfully", HttpStatus.OK); }
        catch (Exception e) { return failure(e); }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id, Authentication auth) {
        try { return success(usersService.getUserById(getOrgId(auth), id), "User fetched successfully", HttpStatus.OK); }
        catch (Exception e) { return failure(e); }
    }

    @GetMapping("/users/by-email")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@RequestParam String email, Authentication auth) {
        try { return success(usersService.getUserByEmail(getOrgId(auth), email), "User fetched successfully", HttpStatus.OK); }
        catch (Exception e) { return failure(e); }
    }

    @GetMapping("/users/exists")
    public ResponseEntity<Map<String, Object>> checkEmailExists(@RequestParam String email, Authentication auth) {
        try { return success(usersService.existsByEmail(getOrgId(auth), email), "Email existence checked successfully", HttpStatus.OK); }
        catch (Exception e) { return failure(e); }
    }

    @PostMapping("/user/create")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Users user, Authentication auth) {
        try {
            Long orgId = getOrgId(auth);
            Users created = usersService.createUser(orgId, user, auth.getName());
            return success(created, "User created successfully", HttpStatus.CREATED);
        } catch (Exception e) { return failure(e); }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody Users user, Authentication auth) {
        try { return success(usersService.updateUser(getOrgId(auth), id, user), "User updated successfully", HttpStatus.OK); }
        catch (Exception e) { return failure(e); }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id, Authentication auth) {
        try {
            usersService.deleteUser(getOrgId(auth), id);
            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("message", "User deleted successfully");
            return ResponseEntity.ok(body);
        } catch (Exception e) { return failure(e); }
    }

    private Long getOrgId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null || auth.getName().trim().isEmpty()) {
            throw new AuthenticationRequiredException("Authentication required");
        }
        String email = auth.getName().trim().toLowerCase();
        UserAccount account = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationRequiredException("Authenticated account not found"));
        if (account.getOrgId() == null) throw new AuthenticationRequiredException("Authenticated account has no organization assigned");
        return account.getOrgId();
    }

    private ResponseEntity<Map<String, Object>> success(Object data, String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", message);
        body.put("data", data);
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<Map<String, Object>> failure(Exception e) {
        HttpStatus status;
        if (e instanceof AuthenticationRequiredException) status = HttpStatus.UNAUTHORIZED;
        else if (e instanceof UsersService.ResourceNotFoundException) status = HttpStatus.NOT_FOUND;
        else if (e instanceof UsersService.DuplicateResourceException) status = HttpStatus.CONFLICT;
        else if (e instanceof IllegalArgumentException) status = HttpStatus.BAD_REQUEST;
        else status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", e.getMessage() == null ? "Request failed" : e.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    private static class AuthenticationRequiredException extends RuntimeException {
        AuthenticationRequiredException(String message) { super(message); }
    }
}
