package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Sprints;
import com.greatleyposhley.professy.entities.Stories;
import com.greatleyposhley.professy.entities.Tasks;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.entities.Users;
import com.greatleyposhley.professy.repositories.SprintsRepository;
import com.greatleyposhley.professy.repositories.StoriesRepository;
import com.greatleyposhley.professy.repositories.TasksRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import com.greatleyposhley.professy.repositories.UsersRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TasksService {

    private final TasksRepository tasksRepository;
    private final StoriesRepository storiesRepository;
    private final SprintsRepository sprintsRepository;
    private final UserAccountRepository userAccountRepository;
    private final UsersRepository usersRepository;

    public TasksService(TasksRepository tasksRepository,
                        StoriesRepository storiesRepository,
                        SprintsRepository sprintsRepository,
                        UserAccountRepository userAccountRepository,
                        UsersRepository usersRepository) {
        this.tasksRepository = tasksRepository;
        this.storiesRepository = storiesRepository;
        this.sprintsRepository = sprintsRepository;
        this.userAccountRepository = userAccountRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTasks(Long orgId, Long sprintId,
                                              Long storyId,
                                              Long epicId,
                                              String status,
                                              String userEmail) {
        getUserAccount(orgId, userEmail);

        /*
         * Preserves the supplied Flask behavior:
         * only sprint_id is used in the repository query.
         */
        List<Tasks> tasks =
                sprintId != null
                        ? tasksRepository.findAllByOrgIdAndSprint_IdOrderByCreatedAtAsc(orgId, sprintId)
                        : tasksRepository.findAllByOrgIdOrderByCreatedAtAsc(orgId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Tasks task : tasks) {
            result.add(toResponse(task));
        }
        return result;
    }

    @Transactional
    public Map<String, Object> createTask(Long orgId, Map<String, Object> payload,
                                          String userEmail) {
        UserAccount userAccount = getUserAccount(orgId, userEmail);

        Long storyId = readLong(payload, "story_id");
        Long sprintId = readLong(payload, "sprint_id");
        Long creatorUserId = readLong(payload, "creator_user_id");
        Long assigneeUserId = readLong(payload, "assignee_user_id");
        Long reporterUserId = readLong(payload, "reporter_user_id");

        String title = readString(payload, "title");
        String description = readNullableString(payload, "description");
        String status = readString(payload, "status");

        if (storyId == null || title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "story_id and title are required fields"
            );
        }

        Stories story = storiesRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));

        Sprints sprint = getOptionalSprint(orgId, sprintId);

        Long effectiveCreatorId =
                creatorUserId != null
                        ? creatorUserId
                        : userAccount.getId();

        Users creator = getUser(orgId, effectiveCreatorId);
        Users assignee = getOptionalUser(orgId, assigneeUserId);
        Users reporter = getOptionalUser(orgId, reporterUserId);

        if (status == null || status.isBlank()) {
            status = "TODO";
        }

        Tasks task = new Tasks();
        task.setOrgId(orgId);
        task.setStory(story);
        task.setSprint(sprint);
        task.setUser(userAccount);
        task.setCreator(creator);
        task.setAssignee(assignee);
        task.setReporter(reporter);
        task.setTitle(title.trim());
        task.setDescription(description);
        task.setStatus(status.trim());

        Tasks saved = tasksRepository.save(task);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", saved.getId());
        result.put("story_id", saved.getStory().getId());
        result.put(
                "sprint_id",
                saved.getSprint() == null ? null : saved.getSprint().getId()
        );
        result.put("title", saved.getTitle());
        result.put("status", saved.getStatus());
        return result;
    }

    @Transactional
    public List<Map<String, Object>> bulkCreateTasks(Long orgId, List<Map<String, Object>> payloads,
                                                     String userEmail) {

        if (payloads == null || payloads.isEmpty()) {
            throw new IllegalArgumentException("At least one task is required");
        }

        List<Map<String, Object>> results = new ArrayList<>();

        for (int i = 0; i < payloads.size(); i++) {
            Map<String, Object> payload = payloads.get(i);

            if (payload == null) {
                throw new IllegalArgumentException(
                        "Task at index " + i + " must not be null"
                );
            }

            results.add(createTask(orgId, payload, userEmail));
        }

        return results;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTasksByStory(Long orgId, Long storyId,
                                                     String userEmail) {
        getUserAccount(orgId, userEmail);

        if (storyId == null || storyId <= 0) {
            throw new IllegalArgumentException("Invalid story id");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Tasks task : tasksRepository.findAllByOrgIdAndStory_IdOrderByCreatedAtAsc(orgId, storyId)) {
            result.add(toResponse(task));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTaskById(Long orgId, Long taskId,
                                           String userEmail) {
        getUserAccount(orgId, userEmail);

        Tasks task = tasksRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        return toResponse(task);
    }

    @Transactional
    public Map<String, Object> updateTask(Long orgId, Long taskId,
                                          Map<String, Object> payload,
                                          String userEmail) {
        getUserAccount(orgId, userEmail);

        Tasks task = tasksRepository.findById(taskId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Task not found or no changes made"
                        )
                );

        String title = readString(payload, "title");
        String description = readNullableString(payload, "description");
        Long sprintId = readLong(payload, "sprint_id");
        String status = readString(payload, "status");
        Long assigneeUserId = readLong(payload, "assignee_user_id");
        Long reporterUserId = readLong(payload, "reporter_user_id");

        if (title == null || title.isBlank()
                || status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "title and status are required fields"
            );
        }

        task.setTitle(title.trim());
        task.setDescription(description);
        task.setSprint(getOptionalSprint(orgId, sprintId));
        task.setStatus(status.trim());
        task.setAssignee(getOptionalUser(orgId, assigneeUserId));
        task.setReporter(getOptionalUser(orgId, reporterUserId));

        Tasks saved = tasksRepository.save(task);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", saved.getId());
        result.put("title", saved.getTitle());
        result.put("status", saved.getStatus());
        result.put(
                "sprint_id",
                saved.getSprint() == null ? null : saved.getSprint().getId()
        );
        result.put(
                "assignee_user_id",
                saved.getAssignee() == null ? null : saved.getAssignee().getId()
        );
        return result;
    }

    @Transactional
    public Map<String, Object> deleteTask(Long orgId, Long taskId,
                                          String userEmail) {
        getUserAccount(orgId, userEmail);

        if (!tasksRepository.existsById(taskId)) {
            throw new IllegalArgumentException("Task not found");
        }

        tasksRepository.deleteById(taskId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Task deleted successfully");
        result.put("id", taskId);
        return result;
    }

    private Map<String, Object> toResponse(Tasks task) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", task.getId());
        result.put(
                "story_id",
                task.getStory() == null ? null : task.getStory().getId()
        );
        result.put(
                "sprint_id",
                task.getSprint() == null ? null : task.getSprint().getId()
        );
        result.put(
                "user_id",
                task.getUser() == null ? null : task.getUser().getId()
        );
        result.put(
                "creator_user_id",
                task.getCreator() == null ? null : task.getCreator().getId()
        );
        result.put(
                "assignee_user_id",
                task.getAssignee() == null ? null : task.getAssignee().getId()
        );
        result.put(
                "reporter_user_id",
                task.getReporter() == null ? null : task.getReporter().getId()
        );
        result.put("title", task.getTitle());
        result.put("description", task.getDescription());
        result.put("status", task.getStatus());
        result.put("created_at", task.getCreatedAt());
        result.put("updated_at", task.getUpdatedAt());
        return result;
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

    private Users getOptionalUser(Long orgId, Long userId) {
        return userId == null ? null : getUser(orgId, userId);
    }

    private Users getUser(Long orgId, Long userId) {
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
}
