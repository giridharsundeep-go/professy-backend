package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Epics;
import com.greatleyposhley.professy.entities.Projects;
import com.greatleyposhley.professy.entities.Sprints;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.EpicsRepository;
import com.greatleyposhley.professy.repositories.ProjectsRepository;
import com.greatleyposhley.professy.repositories.SprintsRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EpicsService {

    private final EpicsRepository epicsRepository;
    private final ProjectsRepository projectsRepository;
    private final SprintsRepository sprintsRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountRepository UserAccountRepository;

    public EpicsService(EpicsRepository epicsRepository,
                        ProjectsRepository projectsRepository,
                        SprintsRepository sprintsRepository,
                        UserAccountRepository userAccountRepository,
                        UserAccountRepository UserAccountRepository) {
        this.epicsRepository = epicsRepository;
        this.projectsRepository = projectsRepository;
        this.sprintsRepository = sprintsRepository;
        this.userAccountRepository = userAccountRepository;
        this.UserAccountRepository = UserAccountRepository;
    }

    @Transactional
    public Map<String, Object> createEpic(Long orgId, Map<String, Object> payload,
                                          String userEmail) {
        UserAccount userAccount = getUserAccount(orgId, userEmail);

        Long projectId = readLong(payload, "project_id");
        Long sprintId = readLong(payload, "sprint_id");
        Long creatorUserId = readLong(payload, "creator_user_id");
        Long assigneeUserId = readLong(payload, "assignee_user_id");
        Long reporterUserId = readLong(payload, "reporter_user_id");

        String epicCode = readString(payload, "epic_code");
        String name = readString(payload, "name");
        String description = readNullableString(payload, "description");
        String status = readString(payload, "status");

        if (projectId == null
                || epicCode == null
                || epicCode.isBlank()
                || name == null
                || name.isBlank()) {
            throw new IllegalArgumentException(
                    "project_id, sprint_id, epic_code, and name are required fields"
            );
        }

        Projects project = getProject(orgId, projectId);

        /*
         * The Flask implementation uses the authenticated user as
         * user_id and defaults creator_user_id to that same id.
         */
        Long effectiveCreatorId =
                creatorUserId != null
                        ? creatorUserId
                        : userAccount.getId();

        UserAccount creator = getUser(orgId, effectiveCreatorId);
        Sprints sprint = getOptionalSprint(orgId, sprintId);
        UserAccount assignee = getOptionalUser(orgId, assigneeUserId);
        UserAccount reporter = getOptionalUser(orgId, reporterUserId);

        if (status == null || status.isBlank()) {
            status = "BACKLOG";
        }

        Epics epic = new Epics();
        epic.setOrgId(orgId);
        epic.setProject(project);
        epic.setSprint(sprint);
        epic.setUserAccount(userAccount);
        epic.setCreator(creator);
        epic.setAssignee(assignee);
        epic.setReporter(reporter);
        epic.setEpicCode(epicCode.trim());
        epic.setName(name.trim());
        epic.setDescription(description);
        epic.setStatus(status.trim());

        Epics saved = epicsRepository.save(epic);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", saved.getId());
        result.put("epic_code", saved.getEpicCode());
        result.put("name", saved.getName());
        return result;
    }

    @Transactional
    public List<Map<String, Object>> bulkCreateEpics(Long orgId, List<Map<String, Object>> payloads,
                                                     String userEmail) {

        if (payloads == null || payloads.isEmpty()) {
            throw new IllegalArgumentException("At least one epic is required");
        }

        List<Map<String, Object>> results = new ArrayList<>();

        for (int i = 0; i < payloads.size(); i++) {
            Map<String, Object> payload = payloads.get(i);

            if (payload == null) {
                throw new IllegalArgumentException(
                        "Epic at index " + i + " must not be null"
                );
            }

            results.add(createEpic(orgId, payload, userEmail));
        }

        return results;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEpicsByProject(Long orgId, Long projectId,
                                                       String userEmail) {
        getUserAccount(orgId, userEmail);
        getProject(orgId, projectId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Epics epic : epicsRepository.findAllByOrgIdAndProject_IdOrderByCreatedAtDesc(orgId, projectId)) {
            result.add(toResponse(epic));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getEpicById(Long orgId, Long epicId,
                                           String userEmail) {
        getUserAccount(orgId, userEmail);

        Epics epic = epicsRepository.findById(epicId)
                .orElseThrow(() -> new IllegalArgumentException("Epic not found"));

        return toResponse(epic);
    }

    @Transactional
    public Map<String, Object> updateEpic(Long orgId, Long epicId,
                                          Map<String, Object> payload,
                                          String userEmail) {
        getUserAccount(orgId, userEmail);

        Epics epic = epicsRepository.findById(epicId)
                .orElseThrow(() -> new IllegalArgumentException("Epic not found"));

        String name = readString(payload, "name");
        String description = readNullableString(payload, "description");
        Long sprintId = readLong(payload, "sprint_id");
        String status = readString(payload, "status");
        Long assigneeUserId = readLong(payload, "assignee_user_id");
        Long reporterUserId = readLong(payload, "reporter_user_id");

        if (name == null || name.isBlank()
                || status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "name and status are required fields"
            );
        }

        epic.setName(name.trim());
        epic.setDescription(description);
        epic.setSprint(getOptionalSprint(orgId, sprintId));
        epic.setStatus(status.trim());
        epic.setAssignee(getOptionalUser(orgId, assigneeUserId));
        epic.setReporter(getOptionalUser(orgId, reporterUserId));

        Epics saved = epicsRepository.save(epic);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", saved.getId());
        result.put("name", saved.getName());
        result.put("status", saved.getStatus());
        result.put(
                "assignee_user_id",
                saved.getAssignee() == null ? null : saved.getAssignee().getId()
        );
        result.put(
                "reporter_user_id",
                saved.getReporter() == null ? null : saved.getReporter().getId()
        );
        return result;
    }

    @Transactional
    public Map<String, Object> deleteEpic(Long orgId, Long epicId,
                                          String userEmail) {
        getUserAccount(orgId, userEmail);

        if (!epicsRepository.existsById(epicId)) {
            throw new IllegalArgumentException("Epic not found");
        }

        epicsRepository.deleteById(epicId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Epic deleted successfully");
        result.put("id", epicId);
        return result;
    }

    private Map<String, Object> toResponse(Epics epic) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", epic.getId());
        result.put(
                "project_id",
                epic.getProject() == null ? null : epic.getProject().getId()
        );
        result.put(
                "sprint_id",
                epic.getSprint() == null ? null : epic.getSprint().getId()
        );
        result.put(
                "user_id",
                epic.getUserAccount() == null ? null : epic.getUserAccount().getId()
        );
        result.put(
                "creator_user_id",
                epic.getCreator() == null ? null : epic.getCreator().getId()
        );
        result.put(
                "assignee_user_id",
                epic.getAssignee() == null ? null : epic.getAssignee().getId()
        );
        result.put(
                "reporter_user_id",
                epic.getReporter() == null ? null : epic.getReporter().getId()
        );
        result.put("epic_code", epic.getEpicCode());
        result.put("name", epic.getName());
        result.put("description", epic.getDescription());
        result.put("status", epic.getStatus());
        result.put("created_at", epic.getCreatedAt());
        result.put("updated_at", epic.getUpdatedAt());
        return result;
    }

    private Projects getProject(Long orgId, Long projectId) {
        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException("Invalid project_id");
        }

        return projectsRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    private Sprints getOptionalSprint(Long orgId, Long sprintId) {
        if (sprintId == null) {
            return null;
        }

        if (sprintId <= 0) {
            throw new IllegalArgumentException("Invalid sprint_id");
        }

        return sprintsRepository.findById(sprintId)
                .orElseThrow(() -> new IllegalArgumentException("Sprint not found"));
    }

    private UserAccount getOptionalUser(Long orgId, Long userId) {
        if (userId == null) {
            return null;
        }

        return getUser(orgId, userId);
    }

    private UserAccount getUser(Long orgId, Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user id");
        }

        return UserAccountRepository.findByOrgIdAndId(orgId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private UserAccount getUserAccount(Long orgId, String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Authenticated user email is required");
        }

        return userAccountRepository.findByOrgIdAndEmail(orgId, email.trim().toLowerCase())
                .orElseThrow(
                        () -> new IllegalArgumentException("Authenticated user not found")
                );
    }

    private String readString(Map<String, Object> payload, String key) {
        if (payload == null || payload.get(key) == null) {
            return null;
        }
        return String.valueOf(payload.get(key)).trim();
    }

    private String readNullableString(Map<String, Object> payload, String key) {
        String value = readString(payload, key);
        return value == null || value.isBlank() ? null : value;
    }

    private Long readLong(Map<String, Object> payload, String key) {
        if (payload == null || payload.get(key) == null) {
            return null;
        }

        Object value = payload.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }

        try {
            return Long.valueOf(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Invalid " + key + ": " + value
            );
        }
    }
}
