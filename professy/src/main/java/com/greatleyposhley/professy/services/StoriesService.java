package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Epics;
import com.greatleyposhley.professy.entities.Projects;
import com.greatleyposhley.professy.entities.Sprints;
import com.greatleyposhley.professy.entities.Stories;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.EpicsRepository;
import com.greatleyposhley.professy.repositories.ProjectsRepository;
import com.greatleyposhley.professy.repositories.SprintsRepository;
import com.greatleyposhley.professy.repositories.StoriesRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoriesService {

    private final StoriesRepository storiesRepository;
    private final ProjectsRepository projectsRepository;
    private final EpicsRepository epicsRepository;
    private final SprintsRepository sprintsRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountRepository usersRepository;

    public StoriesService(StoriesRepository storiesRepository,
                          ProjectsRepository projectsRepository,
                          EpicsRepository epicsRepository,
                          SprintsRepository sprintsRepository,
                          UserAccountRepository userAccountRepository,
                          UserAccountRepository usersRepository) {
        this.storiesRepository = storiesRepository;
        this.projectsRepository = projectsRepository;
        this.epicsRepository = epicsRepository;
        this.sprintsRepository = sprintsRepository;
        this.userAccountRepository = userAccountRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional
    public Map<String, Object> createStory(Long orgId, Map<String, Object> payload,
                                           String userEmail) {
        UserAccount userAccount = getUserAccount(orgId, userEmail);

        Long projectId = readLong(payload, "project_id");
        Long epicId = readLong(payload, "epic_id");
        Long sprintId = readLong(payload, "sprint_id");
        Long creatorUserId = readLong(payload, "creator_user_id");
        Long assigneeUserId = readLong(payload, "assignee_user_id");
        Long reporterUserId = readLong(payload, "reporter_user_id");
        Integer storyPoints = readInteger(payload, "story_points");

        String title = readString(payload, "title");
        String description = readNullableString(payload, "description");
        String status = readString(payload, "status");
        String priority = readString(payload, "priority");

        if (projectId == null || title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "project_id and title are required fields"
            );
        }

        Projects project = getProject(orgId, projectId);

        Long effectiveCreatorId =
                creatorUserId != null
                        ? creatorUserId
                        : userAccount.getId();

        UserAccount creator = getUser(orgId, effectiveCreatorId);
        Epics epic = getOptionalEpic(orgId, epicId);
        Sprints sprint = getOptionalSprint(orgId, sprintId);
        UserAccount assignee = getOptionalUser(orgId, assigneeUserId);
        UserAccount reporter = getOptionalUser(orgId, reporterUserId);

        if (storyPoints == null) {
            storyPoints = 0;
        }

        if (status == null || status.isBlank()) {
            status = "BACKLOG";
        }

        if (priority == null || priority.isBlank()) {
            priority = "MEDIUM";
        }

        Stories story = new Stories();
        story.setOrgId(orgId);
        story.setProject(project);
        story.setEpic(epic);
        story.setSprint(sprint);
        story.setUserAccount(userAccount);
        story.setCreator(creator);
        story.setAssignee(assignee);
        story.setReporter(reporter);
        story.setTitle(title.trim());
        story.setDescription(description);
        story.setStoryPoints(storyPoints);
        story.setStatus(status.trim());
        story.setPriority(priority.trim());

        Stories saved = storiesRepository.save(story);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", saved.getId());
        result.put("title", saved.getTitle());
        result.put("status", saved.getStatus());
        result.put(
                "sprint_id",
                saved.getSprint() == null ? null : saved.getSprint().getId()
        );
        return result;
    }

    @Transactional
    public List<Map<String, Object>> bulkCreateStories(Long orgId, List<Map<String, Object>> payloads,
                                                       String userEmail) {

        if (payloads == null || payloads.isEmpty()) {
            throw new IllegalArgumentException("At least one story is required");
        }

        List<Map<String, Object>> results = new ArrayList<>();

        for (int i = 0; i < payloads.size(); i++) {
            Map<String, Object> payload = payloads.get(i);

            if (payload == null) {
                throw new IllegalArgumentException(
                        "Story at index " + i + " must not be null"
                );
            }

            results.add(createStory(orgId, payload, userEmail));
        }

        return results;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getStoriesByProject(Long orgId, Long projectId,
                                                         String userEmail) {
        getUserAccount(orgId, userEmail);
        getProject(orgId, projectId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Stories story : storiesRepository.findAllByOrgIdAndProject_IdOrderByCreatedAtAsc(orgId, projectId)) {
            result.add(toResponse(story));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getStoriesBySprint(Long orgId, Long sprintId,
                                                        String userEmail) {
        getUserAccount(orgId, userEmail);

        if (sprintId == null || sprintId <= 0) {
            throw new IllegalArgumentException("Invalid sprint id");
        }

        if (!sprintsRepository.existsById(sprintId)) {
            throw new IllegalArgumentException("Sprint not found");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Stories story : storiesRepository.findAllByOrgIdAndSprint_IdOrderByPriorityDescCreatedAtAsc(orgId, sprintId)) {
            result.add(toResponse(story));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStoryById(Long orgId, Long storyId,
                                            String userEmail) {
        getUserAccount(orgId, userEmail);

        Stories story = storiesRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));

        return toResponse(story);
    }

    @Transactional
    public Map<String, Object> updateStory(Long orgId, Long storyId,
                                           Map<String, Object> payload,
                                           String userEmail) {
        getUserAccount(orgId, userEmail);

        Stories story = storiesRepository.findById(storyId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Story not found or no changes made"
                        )
                );

        String title = readString(payload, "title");
        String description = readNullableString(payload, "description");
        Integer storyPoints = readInteger(payload, "story_points");
        String status = readString(payload, "status");
        String priority = readString(payload, "priority");
        Long epicId = readLong(payload, "epic_id");
        Long sprintId = readLong(payload, "sprint_id");
        Long assigneeUserId = readLong(payload, "assignee_user_id");
        Long reporterUserId = readLong(payload, "reporter_user_id");

        if (title == null || title.isBlank()
                || status == null || status.isBlank()
                || priority == null || priority.isBlank()) {
            throw new IllegalArgumentException(
                    "title, status, and priority are required fields"
            );
        }

        if (storyPoints == null) {
            storyPoints = 0;
        }

        story.setTitle(title.trim());
        story.setDescription(description);
        story.setStoryPoints(storyPoints);
        story.setStatus(status.trim());
        story.setPriority(priority.trim());
        story.setEpic(getOptionalEpic(orgId, epicId));
        story.setSprint(getOptionalSprint(orgId, sprintId));
        story.setAssignee(getOptionalUser(orgId, assigneeUserId));
        story.setReporter(getOptionalUser(orgId, reporterUserId));

        Stories saved = storiesRepository.save(story);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", saved.getId());
        result.put("title", saved.getTitle());
        result.put("status", saved.getStatus());
        result.put(
                "sprint_id",
                saved.getSprint() == null ? null : saved.getSprint().getId()
        );
        result.put("story_points", saved.getStoryPoints());
        return result;
    }

    @Transactional
    public Map<String, Object> deleteStory(Long orgId, Long storyId,
                                           String userEmail) {
        getUserAccount(orgId, userEmail);

        if (!storiesRepository.existsById(storyId)) {
            throw new IllegalArgumentException("Story not found");
        }

        storiesRepository.deleteById(storyId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Story deleted successfully");
        result.put("id", storyId);
        return result;
    }

    private Map<String, Object> toResponse(Stories story) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", story.getId());
        result.put(
                "project_id",
                story.getProject() == null ? null : story.getProject().getId()
        );
        result.put(
                "epic_id",
                story.getEpic() == null ? null : story.getEpic().getId()
        );
        result.put(
                "sprint_id",
                story.getSprint() == null ? null : story.getSprint().getId()
        );
        result.put(
                "user_id",
                story.getUserAccount() == null ? null : story.getUserAccount().getId()
        );
        result.put(
                "creator_user_id",
                story.getCreator() == null ? null : story.getCreator().getId()
        );
        result.put(
                "assignee_user_id",
                story.getAssignee() == null ? null : story.getAssignee().getId()
        );
        result.put(
                "reporter_user_id",
                story.getReporter() == null ? null : story.getReporter().getId()
        );
        result.put("title", story.getTitle());
        result.put("description", story.getDescription());
        result.put("story_points", story.getStoryPoints());
        result.put("status", story.getStatus());
        result.put("priority", story.getPriority());
        result.put("created_at", story.getCreatedAt());
        result.put("updated_at", story.getUpdatedAt());
        return result;
    }

    private Projects getProject(Long orgId, Long projectId) {
        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException("Invalid project_id");
        }

        return projectsRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    private Epics getOptionalEpic(Long orgId, Long epicId) {
        if (epicId == null) {
            return null;
        }

        if (epicId <= 0) {
            throw new IllegalArgumentException("Invalid epic_id");
        }

        return epicsRepository.findById(epicId)
                .orElseThrow(() -> new IllegalArgumentException("Epic not found"));
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
        return userId == null ? null : getUser(orgId, userId);
    }

    private UserAccount getUser(Long orgId, Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user id");
        }

        return usersRepository.findById(userId)
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

    private Integer readInteger(Map<String, Object> payload, String key) {
        if (payload == null || payload.get(key) == null) {
            return null;
        }

        Object value = payload.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.valueOf(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Invalid " + key + ": " + value
            );
        }
    }
}
