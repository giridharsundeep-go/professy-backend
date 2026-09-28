package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.services.ProjectTeamsService;
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
@RequestMapping("/api/project-teams")
public class ProjectTeamsController {

    private final ProjectTeamsService projectTeamsService;

    public ProjectTeamsController(ProjectTeamsService projectTeamsService) {
        this.projectTeamsService = projectTeamsService;
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncProjectTeams(
            @RequestBody Map<String, Object> payload,
            Authentication authentication
    ) {
        try {
            Long projectId = readLong(payload, "project_id");
            List<Long> teamIds = readIdList(payload, "team_ids");
            Map<String, Object> data = projectTeamsService.syncTeams(
                    projectId,
                    teamIds,
                    authentication.getName()
            );
            return success(data, "Project team allocations synchronized successfully", HttpStatus.OK);
        } catch (ProjectsService.ResourceNotFoundException ex) {
            return error(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> getTeamsByProject(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        try {
            return success(
                    projectTeamsService.getTeamsByProject(projectId, authentication.getName()),
                    "Project teams fetched successfully",
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

    @DeleteMapping("/{projectId}/remove/{teamId}")
    public ResponseEntity<Map<String, Object>> removeTeam(
            @PathVariable Long projectId,
            @PathVariable Long teamId,
            Authentication authentication
    ) {
        try {
            return success(
                    projectTeamsService.removeTeamFromProject(projectId, teamId, authentication.getName()),
                    "Team removed from project successfully",
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
