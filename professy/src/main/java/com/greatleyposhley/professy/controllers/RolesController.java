
package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.Roles;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import com.greatleyposhley.professy.services.RolesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RolesController {

    private final RolesService rolesService;
    private final UserAccountRepository userAccountRepository;

    public RolesController(
            RolesService rolesService,
            UserAccountRepository userAccountRepository) {
        this.rolesService = rolesService;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRoles(
            Authentication authentication) {
        try {
            Long orgId = getOrgId(authentication);
            List<Roles> roles = rolesService.getAllRoles(orgId);

            return response(HttpStatus.OK, true,
                    "Roles fetched successfully", roles);
        } catch (RuntimeException e) {
            return error(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoleById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long orgId = getOrgId(authentication);
            Roles role = rolesService.getRoleById(orgId, id);

            return response(HttpStatus.OK, true,
                    "Role fetched successfully", role);
        } catch (RuntimeException e) {
            return error(e);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(
            @RequestBody Roles role,
            Authentication authentication) {
        try {
            requireAuthentication(authentication);

            Long orgId = getOrgId(authentication);
            String userEmail = authentication.getName();

            Roles created = rolesService.createRole(orgId, role, userEmail);

            return response(HttpStatus.CREATED, true,
                    "Role created successfully", created);
        } catch (RuntimeException e) {
            return error(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable Long id,
            @RequestBody Roles role,
            Authentication authentication) {
        try {
            Long orgId = getOrgId(authentication);
            Roles updated = rolesService.updateRole(orgId, id, role);

            return response(HttpStatus.OK, true,
                    "Role updated successfully", updated);
        } catch (RuntimeException e) {
            return error(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRole(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long orgId = getOrgId(authentication);
            rolesService.deleteRole(orgId, id);

            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("message", "Role deleted successfully");

            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            return error(e);
        }
    }

    private void requireAuthentication(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            throw new UnauthenticatedException("Authentication required");
        }
    }

    private Long getOrgId(Authentication authentication) {
        requireAuthentication(authentication);

        String email = authentication.getName().trim().toLowerCase();

        return userAccountRepository.findByEmail(email)
                .map(UserAccount -> UserAccount.getOrgId())
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));
    }

    private ResponseEntity<Map<String, Object>> response(
            HttpStatus status,
            boolean success,
            String message,
            Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", success);
        body.put("message", message);
        body.put("data", data);

        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<Map<String, Object>> error(RuntimeException e) {
        HttpStatus status = e instanceof UnauthenticatedException
                ? HttpStatus.UNAUTHORIZED
                : "Role not found".equals(e.getMessage())
                ? HttpStatus.NOT_FOUND
                : "Role already exists".equals(e.getMessage())
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", e.getMessage());

        return ResponseEntity.status(status).body(body);
    }

    private static class UnauthenticatedException extends RuntimeException {
        UnauthenticatedException(String message) {
            super(message);
        }
    }
}
