package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.services.StoriesService;
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
public class StoriesController {

    private final StoriesService storiesService;
    private final UserAccountRepository userAccountRepository;

    public StoriesController(StoriesService storiesService,
                             UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        this.storiesService = storiesService;
    }

    @PostMapping("/stories/create")
    public ResponseEntity<Map<String, Object>> createStory(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    storiesService.createStory(
                            orgId,
                            payload,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Story created successfully",
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/stories/bulk-create")
    public ResponseEntity<Map<String, Object>> bulkCreateStories(
            @RequestBody List<Map<String, Object>> payloads,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            List<Map<String, Object>> data =
                    storiesService.bulkCreateStories(
                            orgId,
                            payloads,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Stories created successfully",
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/projects/{projectId}/stories")
    public ResponseEntity<Map<String, Object>> getProjectStories(
            @PathVariable Long projectId,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            List<Map<String, Object>> data =
                    storiesService.getStoriesByProject(
                            orgId,
                            projectId,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Stories fetched successfully",
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sprints/{sprintId}/stories")
    public ResponseEntity<Map<String, Object>> getSprintStories(
            @PathVariable Long sprintId,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            List<Map<String, Object>> data =
                    storiesService.getStoriesBySprint(
                            orgId,
                            sprintId,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Stories fetched successfully",
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stories/{storyId}")
    public ResponseEntity<Map<String, Object>> getStory(
            @PathVariable Long storyId,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    storiesService.getStoryById(
                            orgId,
                            storyId,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Story fetched successfully",
                    HttpStatus.OK
            );
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/stories/{storyId}")
    public ResponseEntity<Map<String, Object>> updateStory(
            @PathVariable Long storyId,
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    storiesService.updateStory(
                            orgId,
                            storyId,
                            payload,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Story updated successfully",
                    HttpStatus.OK
            );
        } catch (IllegalArgumentException ex) {
            HttpStatus status =
                    ex.getMessage() != null
                            && ex.getMessage().startsWith("Story not found")
                            ? HttpStatus.NOT_FOUND
                            : HttpStatus.BAD_REQUEST;

            return error(ex.getMessage(), status);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/stories/{storyId}")
    public ResponseEntity<Map<String, Object>> deleteStory(
            @PathVariable Long storyId,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    storiesService.deleteStory(
                            orgId,
                            storyId,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Story deleted successfully",
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
