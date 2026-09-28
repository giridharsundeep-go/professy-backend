package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.services.TeamsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamsController {

    private final TeamsService teamsService;

    public TeamsController(
            TeamsService teamsService) {

        this.teamsService = teamsService;
    }

    // ============================================================
    // GET ALL TEAMS + MEMBERS
    // ============================================================

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTeams(
            Authentication authentication) {

        try {

            String userEmail =
                    authentication.getName();

            List<Map<String, Object>> teams =
                    teamsService.getAllTeams(
                            userEmail
                    );

            return response(
                    teams,
                    "Teams fetched successfully",
                    HttpStatus.OK
            );

        } catch (Exception e) {

            return error(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // ============================================================
    // GET ONE TEAM + MEMBERS
    // ============================================================

    @GetMapping("/{teamId}")
    public ResponseEntity<Map<String, Object>> getTeam(
            @PathVariable Long teamId,
            Authentication authentication) {

        try {

            String userEmail =
                    authentication.getName();

            Map<String, Object> team =
                    teamsService.getTeam(
                            teamId,
                            userEmail
                    );

            return response(
                    team,
                    "Team fetched successfully",
                    HttpStatus.OK
            );

        } catch (Exception e) {

            return error(
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // ============================================================
    // CREATE TEAM + MEMBERS
    //
    // POST /api/teams
    //
    // {
    //   "name": "Backend Team",
    //   "description": "Backend developers",
    //   "memberIds": [2, 5, 7]
    // }
    // ============================================================

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTeam(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        try {

            String userEmail =
                    authentication.getName();

            String name =
                    request.get("name") != null
                            ? request.get("name").toString()
                            : null;

            String description =
                    request.get("description") != null
                            ? request.get("description").toString()
                            : null;

            List<Long> memberIds =
                    extractMemberIds(
                            request.get("memberIds")
                    );

            Map<String, Object> team =
                    teamsService.createTeam(
                            name,
                            description,
                            memberIds,
                            userEmail
                    );

            return response(
                    team,
                    "Team created successfully",
                    HttpStatus.CREATED
            );

        } catch (Exception e) {

            return error(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // ============================================================
    // UPDATE TEAM + MEMBERS
    //
    // PUT /api/teams/{teamId}
    //
    // {
    //   "name": "Backend Platform",
    //   "description": "Platform team",
    //   "memberIds": [2, 3, 7, 9]
    // }
    // ============================================================

    @PutMapping("/{teamId}")
    public ResponseEntity<Map<String, Object>> updateTeam(
            @PathVariable Long teamId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        try {

            String userEmail =
                    authentication.getName();

            String name =
                    request.get("name") != null
                            ? request.get("name").toString()
                            : null;

            String description =
                    request.get("description") != null
                            ? request.get("description").toString()
                            : null;

            List<Long> memberIds =
                    extractMemberIds(
                            request.get("memberIds")
                    );

            Map<String, Object> team =
                    teamsService.updateTeam(
                            teamId,
                            name,
                            description,
                            memberIds,
                            userEmail
                    );

            return response(
                    team,
                    "Team updated successfully",
                    HttpStatus.OK
            );

        } catch (Exception e) {

            return error(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // ============================================================
    // DELETE TEAM + MEMBERS
    // ============================================================

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Map<String, Object>> deleteTeam(
            @PathVariable Long teamId,
            Authentication authentication) {

        try {

            String userEmail =
                    authentication.getName();

            teamsService.deleteTeam(
                    teamId,
                    userEmail
            );

            return response(
                    null,
                    "Team deleted successfully",
                    HttpStatus.OK
            );

        } catch (Exception e) {

            return error(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // ============================================================
    // EXTRACT USER IDS
    // ============================================================

    private List<Long> extractMemberIds(
            Object value) {

        if (!(value instanceof List<?> list)) {
            return List.of();
        }

        return list.stream()
                .filter(item -> item != null)
                .map(item ->
                        Long.valueOf(
                                item.toString()
                        )
                )
                .distinct()
                .toList();
    }

    // ============================================================
    // SUCCESS RESPONSE
    // ============================================================

    private ResponseEntity<Map<String, Object>> response(
            Object data,
            String message,
            HttpStatus status) {

        Map<String, Object> result =
                new HashMap<>();

        result.put(
                "data",
                data
        );

        result.put(
                "message",
                message
        );

        result.put(
                "success",
                true
        );

        return ResponseEntity
                .status(status)
                .body(result);
    }

    // ============================================================
    // ERROR RESPONSE
    // ============================================================

    private ResponseEntity<Map<String, Object>> error(
            String message,
            HttpStatus status) {

        Map<String, Object> result =
                new HashMap<>();

        result.put(
                "data",
                null
        );

        result.put(
                "message",
                message != null
                        ? message
                        : "Request failed"
        );

        result.put(
                "success",
                false
        );

        return ResponseEntity
                .status(status)
                .body(result);
    }
}