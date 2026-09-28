package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.services.ProjectIndividualMembersService;
import com.greatleyposhley.professy.services.ProjectsService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project-individuals")
public class ProjectIndividualMembersController {

    private final ProjectIndividualMembersService service;

    public ProjectIndividualMembersController(ProjectIndividualMembersService service) {
        this.service = service;
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncIndividuals(
            @RequestBody Map<String, Object> payload,
            Authentication authentication
    ) {
        try {
            Long projectId = readLong(payload, "project_id");
            List<Long> userAccountIds = readIdList(payload, "user_account_ids");

            return success(
                    service.syncIndividuals(projectId, userAccountIds, authentication.getName()),
                    "Project individual member allocations synchronized successfully",
                    HttpStatus.OK
            );
        } catch (ProjectsService.ResourceNotFoundException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> getIndividuals(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        try {
            return success(
                    service.getIndividualsByProject(projectId, authentication.getName()),
                    "Project individual members fetched successfully",
                    HttpStatus.OK
            );
        } catch (ProjectsService.ResourceNotFoundException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{projectId}/remove/{userAccountId}")
    public ResponseEntity<Map<String, Object>> removeIndividual(
            @PathVariable Long projectId,
            @PathVariable Long userAccountId,
            Authentication authentication
    ) {
        try {
            return success(
                    service.removeIndividual(projectId, userAccountId, authentication.getName()),
                    "Individual specialist removed from project successfully",
                    HttpStatus.OK
            );
        } catch (ProjectsService.ResourceNotFoundException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Long readLong(Map<String, Object> payload, String key) {
        if (payload == null || payload.get(key) == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        Object value = payload.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }

    private List<Long> readIdList(Map<String, Object> payload, String key) {
        if (payload == null || payload.get(key) == null) {
            throw new IllegalArgumentException(key + " must be a list");
        }
        Object raw = payload.get(key);
        if (!(raw instanceof List<?> list)) {
            throw new IllegalArgumentException(key + " must be a list");
        }
        return list.stream().map(item -> {
            if (item instanceof Number number) {
                return number.longValue();
            }
            return Long.valueOf(item.toString());
        }).toList();
    }

    private ResponseEntity<Map<String, Object>> success(Object data, String message, HttpStatus status) {
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("data", data);
        response.put("message", message);
        response.put("success", true);
        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<Map<String, Object>> error(String message, HttpStatus status) {
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("data", null);
        response.put("message", message == null ? "Request failed" : message);
        response.put("success", false);
        return ResponseEntity.status(status).body(response);
    }
}
