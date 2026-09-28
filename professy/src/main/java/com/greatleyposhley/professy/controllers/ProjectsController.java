package com.greatleyposhley.professy.controllers;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectsController {

    private final ProjectsService projectsService;

    public ProjectsController(ProjectsService projectsService) {
        this.projectsService = projectsService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createProject(
            @RequestBody Map<String, Object> payload,
            Authentication authentication
    ) {
        try {
            Map<String, Object> data = projectsService.createProject(payload, authentication.getName());
            return success(data, "Project created successfully", HttpStatus.CREATED);
        } catch (ProjectsService.ResourceNotFoundException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProjects(Authentication authentication) {
        try {
            List<Map<String, Object>> data = projectsService.getProjects(authentication.getName());
            return success(data, "Projects fetched successfully", HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<Map<String, Object>> getProjectsByProduct(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        try {
            List<Map<String, Object>> data = projectsService.getProjectsByProduct(productId, authentication.getName());
            return success(data, "Projects fetched successfully", HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> getProjectById(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        try {
            Map<String, Object> data = projectsService.getProjectById(projectId, authentication.getName());
            return success(data, "Project fetched successfully", HttpStatus.OK);
        } catch (ProjectsService.ResourceNotFoundException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> updateProject(
            @PathVariable Long projectId,
            @RequestBody Map<String, Object> payload,
            Authentication authentication
    ) {
        try {
            Map<String, Object> data = projectsService.updateProject(projectId, payload, authentication.getName());
            return success(data, "Project updated successfully", HttpStatus.OK);
        } catch (ProjectsService.ResourceNotFoundException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> deleteProject(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        try {
            Map<String, Object> data = projectsService.deleteProject(projectId, authentication.getName());
            return success(data, "Project deleted successfully", HttpStatus.OK);
        } catch (ProjectsService.ResourceNotFoundException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
