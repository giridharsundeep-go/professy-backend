package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.services.EpicsService;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EpicsController {

    private final EpicsService epicsService;
    private final UserAccountRepository userAccountRepository;

    public EpicsController(EpicsService epicsService,
                           UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        this.epicsService = epicsService;
    }

    @PostMapping("/epics/create")
    public ResponseEntity<Map<String, Object>> createEpic(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    epicsService.createEpic(orgId,
                            payload, authentication.getName());

            return success(
                    data,
                    "Epic created successfully",
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/epics/bulk-create")
    public ResponseEntity<Map<String, Object>> bulkCreateEpics(
            @RequestBody List<Map<String, Object>> payloads,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            List<Map<String, Object>> data =
                    epicsService.bulkCreateEpics(
                            orgId,
                            payloads,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Epics created successfully",
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/projects/{projectId}/epics")
    public ResponseEntity<Map<String, Object>> getProjectEpics(
            @PathVariable Long projectId,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            List<Map<String, Object>> data =
                    epicsService.getEpicsByProject(
                            orgId,
                            projectId,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Epics fetched successfully",
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/epics/{epicId}")
    public ResponseEntity<Map<String, Object>> getEpic(
            @PathVariable Long epicId,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    epicsService.getEpicById(
                            orgId,
                            epicId,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Epic fetched successfully",
                    HttpStatus.OK
            );
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/epics/{epicId}")
    public ResponseEntity<Map<String, Object>> updateEpic(
            @PathVariable Long epicId,
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    epicsService.updateEpic(
                            orgId,
                            epicId,
                            payload,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Epic updated successfully",
                    HttpStatus.OK
            );
        } catch (IllegalArgumentException ex) {
            HttpStatus status =
                    "Epic not found".equals(ex.getMessage())
                            ? HttpStatus.NOT_FOUND
                            : HttpStatus.BAD_REQUEST;

            return error(ex.getMessage(), status);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/epics/{epicId}")
    public ResponseEntity<Map<String, Object>> deleteEpic(
            @PathVariable Long epicId,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    epicsService.deleteEpic(
                            orgId,
                            epicId,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Epic deleted successfully",
                    HttpStatus.OK
            );
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, Object>> success(
            Object data,
            String message,
            HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("message", message);
        response.put("success", true);

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private ResponseEntity<Map<String, Object>> error(
            String message,
            HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", null);
        response.put(
                "message",
                message == null || message.isBlank()
                        ? "Request failed"
                        : message
        );
        response.put("success", false);

        return ResponseEntity
                .status(status)
                .body(response);
    }


    private Long getOrgId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentication required");
        }

        String email = authentication.getName();

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Authenticated user is required");
        }

        return userAccountRepository
                .findByEmail(email.trim().toLowerCase())
                .map(account -> account.getOrgId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Authenticated user not found")
                );
    }
}
