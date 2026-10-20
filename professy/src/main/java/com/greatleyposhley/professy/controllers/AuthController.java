
package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.Org;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.entities.dtos.OrgValidateRequest;
import com.greatleyposhley.professy.entities.dtos.OrgValidateResponse;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import com.greatleyposhley.professy.services.AuthService;
import com.greatleyposhley.professy.services.OrgService;
import com.greatleyposhley.professy.services.UserAccountService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final OrgService orgService;
    private final UserAccountService userService;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthService authService,
            OrgService orgService,
            UserAccountService userService,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.orgService = orgService;
        this.userService = userService;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------------- LOGIN ----------------

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, Object> request) {

        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (RuntimeException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", ex.getMessage());

            if ("User not found".equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            if ("Invalid credentials".equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            if ("User account is inactive".equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ---------------- ORGANISATION VALIDATION ----------------

    @PostMapping("/organisation/validate")
    public ResponseEntity<OrgValidateResponse> validateOrganisation(
            @RequestBody OrgValidateRequest request) {

        return ResponseEntity.ok(
                orgService.validateOrganisation(request.getSlug()));
    }

    // ---------------- GET ORGANISATION ----------------

    @GetMapping("/organisation/{slug}")
    public ResponseEntity<Org> getOrgBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(orgService.getOrgBySlug(slug));
    }

    // ---------------- CREATE ORGANISATION ----------------

    @PostMapping("/organisation")
    public ResponseEntity<Org> createOrganisation(
            @RequestBody Org request) {

        Org org = orgService.createOrganisation(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(org);
    }

    // ---------------- GET USERS ----------------


    @GetMapping("/user/master/accounts")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam("orgId") Long orgId) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (orgId == null || orgId <= 0) {
                response.put("success", false);
                response.put("message", "Valid orgId is required");

                return ResponseEntity.badRequest().body(response);
            }

            List<UserAccount> users = userService.getAllUsers(orgId);

            // Do not expose password hashes.
            users.forEach(user -> user.setPassword(null));

            response.put("data", users);
            response.put("success", true);
            response.put("message", "Users fetched successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());

            return ResponseEntity.badRequest().body(response);

        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Failed to fetch users");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }


    // ---------------- CREATE USER ----------------

    @PostMapping("/user/master/account/create")
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestBody UserAccount user, @RequestParam("orgId") Long orgId) {

        Map<String, Object> response = new HashMap<>();

        try {

            if (user.getFirstName() == null
                    || user.getFirstName().trim().isEmpty()
                    || user.getLastName() == null
                    || user.getLastName().trim().isEmpty()
                    || user.getEmail() == null
                    || user.getEmail().trim().isEmpty()
                    || user.getPhone() == null
                    || user.getPhone().trim().isEmpty()
                    || user.getGender() == null
                    || user.getPassword() == null
                    || user.getPassword().isBlank()) {

                response.put("success", false);
                response.put("message",
                        "All required user fields must be provided");

                return ResponseEntity.badRequest().body(response);
            }

            String email = user.getEmail()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            if (userAccountRepository.findByEmail(email).isPresent()) {
                response.put("success", false);
                response.put("message", "Email already exists");

                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(response);
            }

            user.setEmail(email);
            user.setPhone(user.getPhone().trim());
            user.setFirstName(user.getFirstName().trim());
            user.setLastName(user.getLastName().trim());
            user.setOrgId(user.getOrgId());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setIsActive(true);
            user.setOrgId(orgId);

            UserAccount savedUser = userService.createUser(user);

            // Never return the stored password hash.
            savedUser.setPassword(null);

            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("data", savedUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(response);

        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Failed to create user");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
