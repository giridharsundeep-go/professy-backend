package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.Roles;
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

    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRoles() {

        List<Roles> roles = rolesService.getAllRoles();

        Map<String, Object> response = new HashMap<>();

        response.put("data", roles);
        response.put("success", true);
        response.put("message", "Roles fetched successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoleById(
            @PathVariable Long id) {

        try {

            Roles role = rolesService.getRoleById(id);

            Map<String, Object> response = new HashMap<>();

            response.put("data", role);
            response.put("success", true);
            response.put("message", "Role fetched successfully");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, Object> response = new HashMap<>();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(
            @RequestBody Roles role,
            Authentication authentication) {

        try {

            if (authentication == null ||
                    !authentication.isAuthenticated()) {

                Map<String, Object> response = new HashMap<>();

                response.put("success", false);
                response.put("message", "Authentication required");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            /*
             * authentication.getName()
             * comes from the email stored in the JWT subject.
             */
            String userEmail = authentication.getName();

            Roles createdRole =
                    rolesService.createRole(role, userEmail);

            Map<String, Object> response = new HashMap<>();

            response.put("data", createdRole);
            response.put("success", true);
            response.put("message", "Role created successfully");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException e) {

            Map<String, Object> response = new HashMap<>();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable Long id,
            @RequestBody Roles role) {

        try {

            Roles updatedRole =
                    rolesService.updateRole(id, role);

            Map<String, Object> response = new HashMap<>();

            response.put("data", updatedRole);
            response.put("success", true);
            response.put("message", "Role updated successfully");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, Object> response = new HashMap<>();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRole(
            @PathVariable Long id) {

        try {

            rolesService.deleteRole(id);

            Map<String, Object> response = new HashMap<>();

            response.put("success", true);
            response.put("message", "Role deleted successfully");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, Object> response = new HashMap<>();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }
}