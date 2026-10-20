package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.services.TasksService;
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
public class TasksController {

    private final TasksService tasksService;
    private final UserAccountRepository userAccountRepository;

    public TasksController(TasksService tasksService,
                           UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        this.tasksService = tasksService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<Map<String, Object>> getTasks(
            @RequestParam(value = "sprint_id", required = false) Long sprintId,
            @RequestParam(value = "sprintId", required = false) Long sprintIdAlias,
            @RequestParam(value = "story_id", required = false) Long storyId,
            @RequestParam(value = "storyId", required = false) Long storyIdAlias,
            @RequestParam(value = "epic_id", required = false) Long epicId,
            @RequestParam(value = "epicId", required = false) Long epicIdAlias,
            @RequestParam(value = "status", required = false) String status,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Long resolvedSprintId =
                    sprintId != null ? sprintId : sprintIdAlias;

            Long resolvedStoryId =
                    storyId != null ? storyId : storyIdAlias;

            Long resolvedEpicId =
                    epicId != null ? epicId : epicIdAlias;

            List<Map<String, Object>> data =
                    tasksService.getTasks(
                            orgId,
                            resolvedSprintId,
                            resolvedStoryId,
                            resolvedEpicId,
                            status,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Tasks fetched successfully",
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping({"/tasks", "/tasks/create"})
    public ResponseEntity<Map<String, Object>> createTask(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    tasksService.createTask(
                            orgId,
                            payload,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Task created successfully",
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/tasks/bulk-create")
    public ResponseEntity<Map<String, Object>> bulkCreateTasks(
            @RequestBody List<Map<String, Object>> payloads,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            List<Map<String, Object>> data =
                    tasksService.bulkCreateTasks(
                            orgId,
                            payloads,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Tasks created successfully",
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stories/{storyId}/tasks")
    public ResponseEntity<Map<String, Object>> getStoryTasks(
            @PathVariable Long storyId,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            List<Map<String, Object>> data =
                    tasksService.getTasksByStory(
                            orgId,
                            storyId,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Tasks fetched successfully",
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Map<String, Object>> getTask(
            @PathVariable Long taskId,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    tasksService.getTaskById(
                            orgId,
                            taskId,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Task fetched successfully",
                    HttpStatus.OK
            );
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<Map<String, Object>> updateTask(
            @PathVariable Long taskId,
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    tasksService.updateTask(
                            orgId,
                            taskId,
                            payload,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Task updated successfully",
                    HttpStatus.OK
            );
        } catch (IllegalArgumentException ex) {
            HttpStatus status =
                    ex.getMessage() != null
                            && ex.getMessage().startsWith("Task not found")
                            ? HttpStatus.NOT_FOUND
                            : HttpStatus.BAD_REQUEST;

            return error(ex.getMessage(), status);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Map<String, Object>> deleteTask(
            @PathVariable Long taskId,
            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        try {
            Map<String, Object> data =
                    tasksService.deleteTask(
                            orgId,
                            taskId,
                            authentication.getName()
                    );

            return success(
                    data,
                    "Task deleted successfully",
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
