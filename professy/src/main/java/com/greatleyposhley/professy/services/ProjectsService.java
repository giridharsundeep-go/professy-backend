package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.ProjectIndividualMembersRepository;
import com.greatleyposhley.professy.repositories.ProjectTeamsRepository;
import com.greatleyposhley.professy.repositories.ProjectsRepository;
import com.greatleyposhley.professy.repositories.SprintsRepository;
import com.greatleyposhley.professy.repositories.TeamMembersRepository;
import com.greatleyposhley.professy.repositories.TeamsRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectsService {

    private final ProjectsRepository projectsRepository;
    private final SprintsRepository sprintsRepository;
    private final ProjectTeamsRepository projectTeamsRepository;
    private final ProjectIndividualMembersRepository projectIndividualMembersRepository;
    private final TeamsRepository teamsRepository;
    private final TeamMembersRepository teamMembersRepository;
    private final UserAccountRepository userAccountRepository;

    public ProjectsService(
            ProjectsRepository projectsRepository,
            SprintsRepository sprintsRepository,
            ProjectTeamsRepository projectTeamsRepository,
            ProjectIndividualMembersRepository projectIndividualMembersRepository,
            TeamsRepository teamsRepository,
            TeamMembersRepository teamMembersRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.projectsRepository = projectsRepository;
        this.sprintsRepository = sprintsRepository;
        this.projectTeamsRepository = projectTeamsRepository;
        this.projectIndividualMembersRepository = projectIndividualMembersRepository;
        this.teamsRepository = teamsRepository;
        this.teamMembersRepository = teamMembersRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProjects(String userEmail) {
        Long userId = getUserId(userEmail);
        return copyRows(projectsRepository.findProjectRowsByUserId(userId));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProjectsByProduct(Long productId, String userEmail) {
        requireId(productId, "product_id");
        getUserId(userEmail);
        return copyRows(projectsRepository.findProjectRowsByProductId(productId));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProjectById(Long projectId, String userEmail) {
        requireId(projectId, "project_id");
        getUserId(userEmail);

        Map<String, Object> project = projectsRepository.findProjectRowById(projectId);
        if (project == null || project.isEmpty()) {
            throw new ResourceNotFoundException("Project not found");
        }

        Map<String, Object> response = new LinkedHashMap<>(project);

        List<Sprints> sprintRows =
                sprintsRepository.findSprintRowsByProjectId(projectId);

        response.put("sprints", copyRowsForSprints(sprintRows));

        response.put("teams", getProjectTeamsAndUsers(projectId));

        response.put(
                "associatedTeamIds",
                projectTeamsRepository.findTeamIdsByProjectId(projectId)
        );

        response.put(
                "associatedUserIds",
                projectIndividualMembersRepository.findUserAccountIdsByProjectId(projectId)
        );

        return response;
    }

    @Transactional
    public Map<String, Object> createProject(
            Map<String, Object> payload,
            String userEmail
    ) {
        Long userId = getUserId(userEmail);

        String name = requiredString(payload, "name");
        Long productId = requiredLong(payload, "product_id");

        int inserted = projectsRepository.insertProject(
                userId,
                productId,
                stringValue(payload, "project_code", ""),
                name,
                nullableString(payload, "description"),
                stringValue(payload, "methodology", "AGILE_SCRUM"),
                stringValue(payload, "priority", "MEDIUM"),
                integerValue(payload, "total_backlog_points", 0),
                nullableInteger(payload, "sprint_duration_weeks"),
                nullableInteger(payload, "target_velocity"),
                booleanValue(payload, "auto_rollover_backlog", true),
                nullableInteger(payload, "computed_sprint_count"),
                nullableInteger(payload, "computed_total_duration_weeks")
        );

        if (inserted == 0) {
            throw new IllegalStateException("Project could not be created");
        }

        Long projectId = projectsRepository.getLastInsertedProjectId();
        if (projectId == null || projectId <= 0) {
            throw new IllegalStateException("Unable to resolve created project id");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", projectId);
        result.put("product_id", productId);
        result.put("name", name);
        result.put("status", "ACTIVE");
        return result;
    }

    @Transactional
    public Map<String, Object> updateProject(
            Long projectId,
            Map<String, Object> payload,
            String userEmail
    ) {
        requireId(projectId, "project_id");
        getUserId(userEmail);

        Map<String, Object> existing = projectsRepository.findProjectRowById(projectId);
        if (existing == null || existing.isEmpty()) {
            throw new ResourceNotFoundException("Project not found");
        }

        String name = requiredString(payload, "name");
        Long productId = requiredLong(payload, "product_id");

        String status = stringValue(payload, "status", stringValue(existing, "status", "ACTIVE"));
        String methodology = stringValue(payload, "methodology", stringValue(existing, "methodology", "AGILE_SCRUM"));
        String priority = stringValue(payload, "priority", stringValue(existing, "priority", "MEDIUM"));

        int updated = projectsRepository.updateProjectRow(
                projectId,
                productId,
                stringValue(payload, "project_code", stringValue(existing, "project_code", "")),
                name,
                nullableString(payload, "description"),
                status,
                methodology,
                priority,
                integerValue(payload, "total_backlog_points", intFrom(existing.get("total_backlog_points"), 0)),
                nullableInteger(payload, "sprint_duration_weeks", integerFrom(existing.get("sprint_duration_weeks"))),
                nullableInteger(payload, "target_velocity", integerFrom(existing.get("target_velocity"))),
                booleanValue(payload, "auto_rollover_backlog", booleanFrom(existing.get("auto_rollover_backlog"), true)),
                nullableInteger(payload, "computed_sprint_count", integerFrom(existing.get("computed_sprint_count"))),
                nullableInteger(payload, "computed_total_duration_weeks", integerFrom(existing.get("computed_total_duration_weeks")))
        );

        if (updated == 0) {
            throw new ResourceNotFoundException("Project not found");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", projectId);
        result.put("product_id", productId);
        result.put("name", name);
        result.put("status", status);
        return result;
    }

    @Transactional
    public Map<String, Object> deleteProject(
            Long projectId,
            String userEmail
    ) {
        requireId(projectId, "project_id");
        getUserId(userEmail);

        int deleted = projectsRepository.deleteProjectRow(projectId);
        if (deleted == 0) {
            throw new ResourceNotFoundException("Project not found");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Project deleted successfully");
        result.put("id", projectId);
        return result;
    }

    private List<Map<String, Object>> getProjectTeamsAndUsers(Long projectId) {
        List<Long> teamIds = projectTeamsRepository.findTeamIdsByProjectId(projectId);
        List<Map<String, Object>> teams = new ArrayList<>();

        for (Long teamId : teamIds) {
            Teams team = teamsRepository.findById(teamId).orElse(null);
            if (team == null) {
                continue;
            }

            Map<String, Object> teamData = new LinkedHashMap<>();
            teamData.put("id", team.getId());
            teamData.put("name", team.getName());
            teamData.put("description", team.getDescription());

            List<Map<String, Object>> members = new ArrayList<>();
            List<TeamMembers> teamMembers = teamMembersRepository.findAllByTeam_Id(teamId);

            for (TeamMembers teamMember : teamMembers) {
                Users user = teamMember.getUser();
                if (user == null) {
                    continue;
                }

                Map<String, Object> userData = new LinkedHashMap<>();
                userData.put("id", user.getId());
                userData.put("name", user.getName());
                userData.put("email", user.getEmail());
                userData.put("role", safeRole(user));
                members.add(userData);
            }

            teamData.put("members", members);
            teams.add(teamData);
        }

        return teams;
    }

    private String safeRole(Users user) {
        try {
            return user.getRole() == null ? "TEAM_MEMBER" : String.valueOf(user.getRole());
        } catch (Exception ignored) {
            return "TEAM_MEMBER";
        }
    }

    private Long getUserId(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Authenticated user email is required");
        }

        UserAccount account = userAccountRepository
                .findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        return account.getId();
    }

    private List<Map<String, Object>> copyRows(List<Map<String, Object>> rows) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (rows == null) {
            return result;
        }
        for (Map<String, Object> row : rows) {
            result.add(new LinkedHashMap<>(row));
        }
        return result;
    }

    private List<Map<String, Object>> copyRowsForSprints(List<Sprints> rows) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Sprints sprint : rows) {
            Map<String, Object> row = new LinkedHashMap<>();

            row.put("id", sprint.getId());
            row.put("project_id",
                    sprint.getProject() != null
                            ? sprint.getProject().getId()
                            : null);
            row.put("user_id",
                    sprint.getUserAccount() != null
                            ? sprint.getUserAccount().getId()
                            : null);
            row.put("sprint_number", sprint.getSprintNumber());
            row.put("name", sprint.getName());
            row.put("status", sprint.getStatus());
            row.put("scheduled_start_date", sprint.getScheduledStartDate());
            row.put("scheduled_end_date", sprint.getScheduledEndDate());
            row.put("actual_start_at", sprint.getActualStartAt());
            row.put("actual_end_at", sprint.getActualEndAt());
            row.put("duration_weeks", sprint.getDurationWeeks());
            row.put("target_velocity", sprint.getTargetVelocity());
            row.put("activation_type", sprint.getActivationType());
            row.put("created_at", sprint.getCreatedAt());
            row.put("updated_at", sprint.getUpdatedAt());

            result.add(row);
        }

        return result;
    }

    private String requiredString(Map<String, Object> payload, String key) {
        String value = nullableString(payload, key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value;
    }

    private Long requiredLong(Map<String, Object> payload, String key) {
        Long value = nullableLong(payload, key);
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value;
    }

    private void requireId(Long value, String field) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    private String nullableString(Map<String, Object> payload, String key) {
        if (payload == null || payload.get(key) == null) {
            return null;
        }
        String value = payload.get(key).toString().trim();
        return value.isEmpty() ? null : value;
    }

    private String stringValue(Map<String, Object> payload, String key, String defaultValue) {
        String value = nullableString(payload, key);
        return value == null ? defaultValue : value;
    }

    private Long nullableLong(Map<String, Object> payload, String key) {
        if (payload == null || payload.get(key) == null) {
            return null;
        }
        Object value = payload.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }

    private Integer nullableInteger(Map<String, Object> payload, String key) {
        return nullableInteger(payload, key, null);
    }

    private Integer nullableInteger(Map<String, Object> payload, String key, Integer defaultValue) {
        if (payload == null || payload.get(key) == null) {
            return defaultValue;
        }
        Object value = payload.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(value.toString());
    }

    private Integer integerValue(Map<String, Object> payload, String key, Integer defaultValue) {
        return nullableInteger(payload, key, defaultValue);
    }

    private Boolean booleanValue(Map<String, Object> payload, String key, Boolean defaultValue) {
        if (payload == null || payload.get(key) == null) {
            return defaultValue;
        }
        Object value = payload.get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.valueOf(value.toString());
    }

    private Integer integerFrom(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(value.toString());
    }

    private int intFrom(Object value, int defaultValue) {
        Integer parsed = integerFrom(value);
        return parsed == null ? defaultValue : parsed;
    }

    private boolean booleanFrom(Object value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return Boolean.parseBoolean(value.toString());
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
