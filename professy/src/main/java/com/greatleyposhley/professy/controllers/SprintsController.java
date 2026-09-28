package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.services.SprintsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sprints")
public class SprintsController {

    private final SprintsService sprintsService;

    public SprintsController(
            SprintsService sprintsService
    ) {
        this.sprintsService = sprintsService;
    }

    // ============================================================
    // GET ALL SPRINTS
    // GET /api/sprints
    // GET /api/sprints?project_id=1
    // GET /api/sprints?projectId=1
    // ============================================================

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSprints(
            @RequestParam(
                    value = "project_id",
                    required = false
            ) Long projectId,

            @RequestParam(
                    value = "projectId",
                    required = false
            ) Long projectIdAlias,

            Authentication authentication
    ) {

        try {

            requireAuthentication(
                    authentication
            );

            Long resolvedProjectId =
                    projectId != null
                            ? projectId
                            : projectIdAlias;

            List<Map<String, Object>> data =
                    resolvedProjectId != null
                            ? sprintsService
                            .getSprintsByProjectId(
                                    resolvedProjectId
                            )
                            : sprintsService
                            .getAllSprints();

            return success(
                    data,
                    "Sprints fetched successfully",
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

        } catch (Exception ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ============================================================
    // CREATE
    // POST /api/sprints/create
    // ============================================================

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createSprint(
            @RequestBody Map<String, Object> payload,
            Authentication authentication
    ) {

        try {

            String email =
                    requireAuthentication(
                            authentication
                    );

            Map<String, Object> data =
                    sprintsService.createSprint(
                            payload,
                            email
                    );

            return success(
                    data,
                    "Sprint created successfully",
                    HttpStatus.CREATED
            );

        } catch (IllegalArgumentException ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

        } catch (Exception ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ============================================================
    // GET BY ID
    // GET /api/sprints/{sprintId}
    // ============================================================

    @GetMapping("/{sprintId}")
    public ResponseEntity<Map<String, Object>> getSprintById(
            @PathVariable Long sprintId,
            Authentication authentication
    ) {

        try {

            requireAuthentication(
                    authentication
            );

            Map<String, Object> data =
                    sprintsService.getSprintById(
                            sprintId
                    );

            return success(
                    data,
                    "Sprint fetched successfully",
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException ex) {

            if ("Sprint not found"
                    .equals(ex.getMessage())) {

                return error(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND
                );
            }

            return error(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

        } catch (Exception ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ============================================================
    // UPDATE
    // PUT /api/sprints/{sprintId}
    // ============================================================

    @PutMapping("/{sprintId}")
    public ResponseEntity<Map<String, Object>> updateSprint(
            @PathVariable Long sprintId,
            @RequestBody Map<String, Object> payload,
            Authentication authentication
    ) {

        try {

            requireAuthentication(
                    authentication
            );

            Map<String, Object> data =
                    sprintsService.updateSprint(
                            sprintId,
                            payload
                    );

            return success(
                    data,
                    "Sprint updated successfully",
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException ex) {

            if ("Sprint not found or no changes made"
                    .equals(ex.getMessage())) {

                return error(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND
                );
            }

            return error(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

        } catch (Exception ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ============================================================
    // DELETE
    // DELETE /api/sprints/{sprintId}
    // ============================================================

    @DeleteMapping("/{sprintId}")
    public ResponseEntity<Map<String, Object>> deleteSprint(
            @PathVariable Long sprintId,
            Authentication authentication
    ) {

        try {

            requireAuthentication(
                    authentication
            );

            Map<String, Object> data =
                    sprintsService.deleteSprint(
                            sprintId
                    );

            return success(
                    data,
                    "Sprint deleted successfully",
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException ex) {

            if ("Sprint not found"
                    .equals(ex.getMessage())) {

                return error(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND
                );
            }

            return error(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

        } catch (Exception ex) {

            return error(
                    ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ============================================================
    // AUTHENTICATION
    // ============================================================

    private String requireAuthentication(
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalArgumentException(
                    "Authentication is required"
            );
        }

        String email =
                authentication.getName();

        if (email == null ||
                email.isBlank()) {

            throw new IllegalArgumentException(
                    "Authenticated user email is required"
            );
        }

        return email;
    }

    // ============================================================
    // RESPONSE WRAPPER
    // Matches your Flask message.success(...)
    // ============================================================

    private ResponseEntity<Map<String, Object>> success(
            Object data,
            String message,
            HttpStatus status
    ) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "data",
                data
        );

        response.put(
                "message",
                message
        );

        response.put(
                "success",
                true
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private ResponseEntity<Map<String, Object>> error(
            String message,
            HttpStatus status
    ) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "data",
                null
        );

        response.put(
                "message",
                message == null
                        ? "Request failed"
                        : message
        );

        response.put(
                "success",
                false
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}