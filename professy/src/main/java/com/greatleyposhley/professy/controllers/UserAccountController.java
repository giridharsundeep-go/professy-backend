package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.components.OrgComponent;
import com.greatleyposhley.professy.entities.OrgRole;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.services.UserAccountService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final OrgComponent orgComponent;

    public UserAccountController(UserAccountService userAccountService,
                                 OrgComponent orgComponent) {
        this.userAccountService = userAccountService;
        this.orgComponent = orgComponent;
    }

    /**
     * Get all users belonging to the authenticated user's organization.
     */
    @GetMapping
    public ResponseEntity<List<UserAccount>> getAllUsers(
            Authentication authentication) {

        UserAccount authenticatedUser = orgComponent.getAuthenticatedUser(authentication);

        List<UserAccount> userAccounts;
        if (authenticatedUser.getOrgRole().equals(OrgRole.SUPER_USER)) {
            userAccounts = userAccountService.getAllUsers(authenticatedUser.getOrgId());
        } else {
            userAccounts = userAccountService.getUsersByOrgRole(authenticatedUser.getOrgId(), OrgRole.ORG_USER);
        }

        return ResponseEntity.ok(userAccounts);
    }

    /**
     * Get active users belonging to the authenticated user's organization.
     */
    @GetMapping("/active")
    public ResponseEntity<List<UserAccount>> getActiveUsers(
            Authentication authentication) {

        UserAccount authenticatedUser = orgComponent.getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                userAccountService.getActiveUsers(authenticatedUser.getOrgId()));
    }

    /**
     * Get users by organization role.
     */
    @GetMapping("/role/{orgRole}")
    public ResponseEntity<List<UserAccount>> getUsersByOrgRole(
            Authentication authentication,
            @PathVariable OrgRole orgRole) {

        Long orgId = orgComponent.getOrgId(authentication);

        return ResponseEntity.ok(
                userAccountService.getUsersByOrgRole(orgId, orgRole));
    }

    /**
     * Get a user by ID within the authenticated user's organization.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(
            Authentication authentication,
            @PathVariable Long userId) {

        Long orgId = orgComponent.getOrgId(authentication);

        return userAccountService.getUserById(orgId, userId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(error("User not found in this organization")));
    }

    /**
     * Get a user by email within the authenticated user's organization.
     */
    @GetMapping("/by-email")
    public ResponseEntity<?> getUserByEmail(
            Authentication authentication,
            @RequestParam String email) {

        Long orgId = orgComponent.getOrgId(authentication);

        return userAccountService.getUserByEmail(orgId, email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(error("User not found in this organization")));
    }

    /**
     * Create a user in the authenticated user's organization.
     * The organization ID is never accepted from the URL or request body.
     */
    @PostMapping
    public ResponseEntity<?> createUser(
            Authentication authentication,
            @RequestBody UserAccount user) {

        System.out.println(user);
        Long orgId = orgComponent.getOrgId(authentication);

        try {
            user.setOrgId(orgId);

            UserAccount createdUser =
                    userAccountService.createUser(user);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(createdUser);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(error(ex.getMessage()));
        }
    }

    /**
     * Update editable profile fields.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            Authentication authentication,
            @PathVariable Long userId,
            @RequestBody UserAccount updates) {

        Long orgId = orgComponent.getOrgId(authentication);

        try {
            UserAccount updatedUser =
                    userAccountService.updateUser(orgId, userId, updates);

            return ResponseEntity.ok(updatedUser);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error(ex.getMessage()));
        }
    }

    /**
     * Deactivate a user instead of deleting the account.
     */
    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<?> deactivateUser(
            Authentication authentication,
            @PathVariable Long userId) {

        Long orgId = orgComponent.getOrgId(authentication);

        try {
            userAccountService.deactivateUser(orgId, userId);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "User deactivated successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error(ex.getMessage()));
        }
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}