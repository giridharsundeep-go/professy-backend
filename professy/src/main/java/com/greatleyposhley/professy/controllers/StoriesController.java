package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.services.StoriesService;
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

    public StoriesController(StoriesService storiesService) {
        this.storiesService = storiesService;
    }

    @PostMapping("/stories/create")
    public ResponseEntity<Map<String, Object>> createStory(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        try {
            Map<String, Object> data =
                    storiesService.createStory(
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

    @GetMapping("/projects/{projectId}/stories")
    public ResponseEntity<Map<String, Object>> getProjectStories(
            @PathVariable Long projectId,
            Authentication authentication) {
        try {
            List<Map<String, Object>> data =
                    storiesService.getStoriesByProject(
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
        try {
            List<Map<String, Object>> data =
                    storiesService.getStoriesBySprint(
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
        try {
            Map<String, Object> data =
                    storiesService.getStoryById(
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
        try {
            Map<String, Object> data =
                    storiesService.updateStory(
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
        try {
            Map<String, Object> data =
                    storiesService.deleteStory(
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
}
