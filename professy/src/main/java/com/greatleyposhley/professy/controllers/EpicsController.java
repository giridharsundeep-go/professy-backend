package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.services.EpicsService;
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

    public EpicsController(EpicsService epicsService) {
        this.epicsService = epicsService;
    }

    @PostMapping("/epics/create")
    public ResponseEntity<Map<String, Object>> createEpic(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        try {
            Map<String, Object> data =
                    epicsService.createEpic(payload, authentication.getName());

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

    @GetMapping("/projects/{projectId}/epics")
    public ResponseEntity<Map<String, Object>> getProjectEpics(
            @PathVariable Long projectId,
            Authentication authentication) {
        try {
            List<Map<String, Object>> data =
                    epicsService.getEpicsByProject(
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
        try {
            Map<String, Object> data =
                    epicsService.getEpicById(
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
        try {
            Map<String, Object> data =
                    epicsService.updateEpic(
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
        try {
            Map<String, Object> data =
                    epicsService.deleteEpic(
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
}
