package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Projects;
import com.greatleyposhley.professy.entities.Sprints;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.ProjectsRepository;
import com.greatleyposhley.professy.repositories.SprintsRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SprintsService {

    private final SprintsRepository sprintsRepository;
    private final ProjectsRepository projectsRepository;
    private final UserAccountRepository userAccountRepository;

    public SprintsService(
            SprintsRepository sprintsRepository,
            ProjectsRepository projectsRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.sprintsRepository = sprintsRepository;
        this.projectsRepository = projectsRepository;
        this.userAccountRepository = userAccountRepository;
    }

    // ============================================================
    // GET ALL SPRINTS
    // ============================================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllSprints() {

        List<Sprints> sprints =
                sprintsRepository.findAllByOrderByIdAsc();

        List<Map<String, Object>> response = new ArrayList<>();

        for (Sprints sprint : sprints) {
            response.add(toResponse(sprint));
        }

        return response;
    }

    // ============================================================
    // GET SPRINTS BY PROJECT
    // ============================================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSprintsByProjectId(
            Long projectId
    ) {

        validateId(projectId);

        List<Sprints> sprints =
                sprintsRepository
                        .findAllByProject_IdOrderBySprintNumberAsc(
                                projectId
                        );

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (Sprints sprint : sprints) {
            response.add(toResponse(sprint));
        }

        return response;
    }

    // ============================================================
    // GET SPRINT BY ID
    // ============================================================

    @Transactional(readOnly = true)
    public Map<String, Object> getSprintById(
            Long sprintId
    ) {

        validateId(sprintId);

        Sprints sprint =
                sprintsRepository
                        .findById(sprintId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sprint not found"
                                )
                        );

        return toResponse(sprint);
    }

    // ============================================================
    // CREATE SPRINT
    // ============================================================

    @Transactional
    public Map<String, Object> createSprint(
            Map<String, Object> payload,
            String userEmail
    ) {

        if (payload == null) {
            throw new IllegalArgumentException(
                    "Sprint data is required"
            );
        }

        Long projectId =
                readLong(
                        payload,
                        "project_id",
                        "projectId"
                );

        String name =
                readString(payload, "name");

        if (projectId == null || name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "project_id and name are required fields"
            );
        }

        Projects project =
                projectsRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Project not found"
                                )
                        );

        UserAccount userAccount =
                getAuthenticatedUser(userEmail);

        Integer sprintNumber =
                readInteger(
                        payload,
                        "sprint_number",
                        "sprintNumber"
                );

        if (sprintNumber == null || sprintNumber <= 0) {
            sprintNumber = 1;
        }

        if (sprintsRepository
                .findByProject_IdAndSprintNumber(
                        projectId,
                        sprintNumber
                )
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Sprint number " +
                            sprintNumber +
                            " already exists for this project"
            );
        }

        LocalDate startDate =
                readDate(
                        payload,
                        "scheduled_start_date",
                        "start_date",
                        "startDate"
                );

        LocalDate endDate =
                readDate(
                        payload,
                        "scheduled_end_date",
                        "end_date",
                        "endDate"
                );

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "start_date and end_date are required fields"
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "end_date cannot be before start_date"
            );
        }

        Sprints sprint =
                new Sprints();

        sprint.setProject(project);
        sprint.setUserAccount(userAccount);
        sprint.setSprintNumber(sprintNumber);

        sprint.setName(
                name.trim()
        );

        sprint.setStatus(
                normalizeStatus(
                        readString(payload, "status"),
                        Sprints.Status.PLANNED
                )
        );

        sprint.setScheduledStartDate(
                startDate
        );

        sprint.setScheduledEndDate(
                endDate
        );

        sprint.setDurationWeeks(
                defaultInteger(
                        readInteger(
                                payload,
                                "duration_weeks",
                                "durationWeeks"
                        ),
                        2
                )
        );

        sprint.setTargetVelocity(
                defaultInteger(
                        readInteger(
                                payload,
                                "target_velocity",
                                "targetVelocity"
                        ),
                        0
                )
        );

        sprint.setActivationType(
                normalizeActivationType(
                        readString(
                                payload,
                                "activation_type",
                                "activationType"
                        ),
                        Sprints.ActivationType.AUTOMATIC
                )
        );

        Sprints saved =
                sprintsRepository.save(sprint);

        return toResponse(saved);
    }

    // ============================================================
    // UPDATE SPRINT
    // ============================================================

    @Transactional
    public Map<String, Object> updateSprint(
            Long sprintId,
            Map<String, Object> payload
    ) {

        validateId(sprintId);

        if (payload == null) {
            throw new IllegalArgumentException(
                    "Sprint data is required"
            );
        }

        String name =
                readString(payload, "name");

        String status =
                readString(payload, "status");

        if (name == null ||
                name.isBlank() ||
                status == null ||
                status.isBlank()) {

            throw new IllegalArgumentException(
                    "name and status are required fields"
            );
        }

        Sprints sprint =
                sprintsRepository
                        .findById(sprintId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sprint not found or no changes made"
                                )
                        );

        LocalDate startDate =
                readDate(
                        payload,
                        "start_date",
                        "scheduled_start_date",
                        "startDate"
                );

        LocalDate endDate =
                readDate(
                        payload,
                        "end_date",
                        "scheduled_end_date",
                        "endDate"
                );

        if (startDate != null &&
                endDate != null &&
                endDate.isBefore(startDate)) {

            throw new IllegalArgumentException(
                    "end_date cannot be before start_date"
            );
        }

        sprint.setName(
                name.trim()
        );

        sprint.setStatus(
                normalizeStatus(
                        status,
                        sprint.getStatus()
                )
        );

        if (startDate != null) {
            sprint.setScheduledStartDate(startDate);
        }

        if (endDate != null) {
            sprint.setScheduledEndDate(endDate);
        }

        return toResponse(
                sprintsRepository.save(sprint)
        );
    }

    // ============================================================
    // DELETE SPRINT
    // ============================================================

    @Transactional
    public Map<String, Object> deleteSprint(
            Long sprintId
    ) {

        validateId(sprintId);

        Sprints sprint =
                sprintsRepository
                        .findById(sprintId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sprint not found"
                                )
                        );

        sprintsRepository.delete(sprint);

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "message",
                "Sprint deleted successfully"
        );

        response.put(
                "id",
                sprintId
        );

        return response;
    }

    // ============================================================
    // BULK INSERT
    // Python: bulk_insert_sprints()
    // ============================================================

    @Transactional
    public int bulkInsertSprints(
            List<Map<String, Object>> sprintList
    ) {

        if (sprintList == null ||
                sprintList.isEmpty()) {

            return 0;
        }

        List<Sprints> entities =
                new ArrayList<>();

        for (Map<String, Object> item : sprintList) {

            Long projectId =
                    readLong(
                            item,
                            "project_id",
                            "projectId"
                    );

            Long userId =
                    readLong(
                            item,
                            "user_id",
                            "userId"
                    );

            if (projectId == null ||
                    userId == null) {

                throw new IllegalArgumentException(
                        "project_id and user_id are required"
                );
            }

            Projects project =
                    projectsRepository
                            .findById(projectId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Project not found: " +
                                                    projectId
                                    )
                            );

            UserAccount userAccount =
                    userAccountRepository
                            .findById(userId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "User account not found: " +
                                                    userId
                                    )
                            );

            Sprints sprint =
                    new Sprints();

            sprint.setProject(project);
            sprint.setUserAccount(userAccount);

            sprint.setSprintNumber(
                    defaultInteger(
                            readInteger(
                                    item,
                                    "sprint_number",
                                    "sprintNumber"
                            ),
                            1
                    )
            );

            sprint.setName(
                    requiredString(item, "name")
            );

            sprint.setStatus(
                    normalizeStatus(
                            readString(item, "status"),
                            Sprints.Status.PLANNED
                    )
            );

            sprint.setScheduledStartDate(
                    requiredDate(
                            item,
                            "scheduled_start_date",
                            "start_date",
                            "startDate"
                    )
            );

            sprint.setScheduledEndDate(
                    requiredDate(
                            item,
                            "scheduled_end_date",
                            "end_date",
                            "endDate"
                    )
            );

            sprint.setDurationWeeks(
                    defaultInteger(
                            readInteger(
                                    item,
                                    "duration_weeks",
                                    "durationWeeks"
                            ),
                            2
                    )
            );

            sprint.setTargetVelocity(
                    defaultInteger(
                            readInteger(
                                    item,
                                    "target_velocity",
                                    "targetVelocity"
                            ),
                            0
                    )
            );

            sprint.setActivationType(
                    normalizeActivationType(
                            readString(
                                    item,
                                    "activation_type",
                                    "activationType"
                            ),
                            Sprints.ActivationType.AUTOMATIC
                    )
            );

            entities.add(sprint);
        }

        return sprintsRepository
                .saveAll(entities)
                .size();
    }

    // ============================================================
    // MANUAL ACTIVATE
    // ============================================================

    @Transactional
    public int activateSprintManually(
            Long sprintId
    ) {

        Sprints sprint =
                sprintsRepository
                        .findById(sprintId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sprint not found"
                                )
                        );

        sprint.setStatus(Sprints.Status.ACTIVE);
        sprint.setActualStartAt(
                LocalDateTime.now()
        );

        sprintsRepository.save(sprint);

        return 1;
    }

    // ============================================================
    // MANUAL COMPLETE
    // ============================================================

    @Transactional
    public int completeSprintManually(
            Long sprintId
    ) {

        Sprints sprint =
                sprintsRepository
                        .findById(sprintId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sprint not found"
                                )
                        );

        sprint.setStatus(Sprints.Status.COMPLETED);
        sprint.setActualEndAt(
                LocalDateTime.now()
        );

        sprintsRepository.save(sprint);

        return 1;
    }

    // ============================================================
    // AUTOMATIC SPRINTS DUE FOR ACTIVATION
    // ============================================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>>
    getAutomaticSprintsToActivate(
            LocalDate checkDate
    ) {

        List<Sprints> sprints =
                sprintsRepository
                        .findAllByStatusAndActivationTypeAndScheduledStartDateLessThanEqual(
                                "PLANNED",
                                "AUTOMATIC",
                                checkDate
                        );

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (Sprints sprint : sprints) {

            Map<String, Object> item =
                    new HashMap<>();

            item.put(
                    "id",
                    sprint.getId()
            );

            item.put(
                    "project_id",
                    sprint.getProject() != null
                            ? sprint.getProject().getId()
                            : null
            );

            item.put(
                    "user_id",
                    sprint.getUserAccount() != null
                            ? sprint.getUserAccount().getId()
                            : null
            );

            item.put(
                    "sprint_number",
                    sprint.getSprintNumber()
            );

            item.put(
                    "name",
                    sprint.getName()
            );

            result.add(item);
        }

        return result;
    }

    // ============================================================
    // SYNC / RECREATE PLANNED SPRINTS
    // Python: sync_and_recreate_planned_sprints()
    // ============================================================

    @Transactional
    public int syncAndRecreatePlannedSprints(
            Long projectId,
            List<Map<String, Object>> sprintList
    ) {

        if (sprintList == null ||
                sprintList.isEmpty()) {

            return 0;
        }

        Projects project =
                projectsRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Project not found"
                                )
                        );

        Set<Integer> processedNumbers =
                new HashSet<>();

        for (Map<String, Object> item : sprintList) {

            Integer sprintNumber =
                    readInteger(
                            item,
                            "sprint_number",
                            "sprintNumber"
                    );

            if (sprintNumber == null ||
                    sprintNumber <= 0) {

                throw new IllegalArgumentException(
                        "sprint_number is required"
                );
            }

            Long userId =
                    readLong(
                            item,
                            "user_id",
                            "userId"
                    );

            if (userId == null) {
                throw new IllegalArgumentException(
                        "user_id is required"
                );
            }

            UserAccount userAccount =
                    userAccountRepository
                            .findById(userId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "User account not found: " +
                                                    userId
                                    )
                            );

            Sprints sprint =
                    sprintsRepository
                            .findByProject_IdAndSprintNumber(
                                    projectId,
                                    sprintNumber
                            )
                            .orElseGet(Sprints::new);

            sprint.setProject(project);
            sprint.setUserAccount(userAccount);
            sprint.setSprintNumber(sprintNumber);

            sprint.setName(
                    requiredString(item, "name")
            );

            sprint.setStatus(
                    normalizeStatus(
                            readString(item, "status"),
                            Sprints.Status.PLANNED
                    )
            );

            sprint.setScheduledStartDate(
                    requiredDate(
                            item,
                            "scheduled_start_date",
                            "start_date",
                            "startDate"
                    )
            );

            sprint.setScheduledEndDate(
                    requiredDate(
                            item,
                            "scheduled_end_date",
                            "end_date",
                            "endDate"
                    )
            );

            sprint.setDurationWeeks(
                    defaultInteger(
                            readInteger(
                                    item,
                                    "duration_weeks",
                                    "durationWeeks"
                            ),
                            2
                    )
            );

            sprint.setTargetVelocity(
                    defaultInteger(
                            readInteger(
                                    item,
                                    "target_velocity",
                                    "targetVelocity"
                            ),
                            0
                    )
            );

            sprint.setActivationType(
                    normalizeActivationType(
                            readString(
                                    item,
                                    "activation_type",
                                    "activationType"
                            ),
                            Sprints.ActivationType.AUTOMATIC
                    )
            );

            sprintsRepository.save(sprint);

            processedNumbers.add(
                    sprintNumber
            );
        }

        // Match Python behavior:
        // delete PLANNED records not present in incoming sprint numbers.
        List<Sprints> plannedSprints =
                sprintsRepository
                        .findAllByProject_IdAndStatus(
                                projectId,
                                "PLANNED"
                        );

        for (Sprints sprint : plannedSprints) {

            if (!processedNumbers.contains(
                    sprint.getSprintNumber()
            )) {
                sprintsRepository.delete(sprint);
            }
        }

        return sprintList.size();
    }

    // ============================================================
    // RESPONSE MAPPING
    // ============================================================

    private Map<String, Object> toResponse(
            Sprints sprint
    ) {

        Map<String, Object> data =
                new LinkedHashMap<>();

        data.put("id", sprint.getId());

        data.put(
                "project_id",
                sprint.getProject() != null
                        ? sprint.getProject().getId()
                        : null
        );

        data.put(
                "user_id",
                sprint.getUserAccount() != null
                        ? sprint.getUserAccount().getId()
                        : null
        );

        data.put(
                "sprint_number",
                sprint.getSprintNumber()
        );

        data.put(
                "name",
                sprint.getName()
        );

        data.put(
                "status",
                sprint.getStatus()
        );

        data.put(
                "scheduled_start_date",
                sprint.getScheduledStartDate()
        );

        data.put(
                "scheduled_end_date",
                sprint.getScheduledEndDate()
        );

        data.put(
                "actual_start_at",
                sprint.getActualStartAt()
        );

        data.put(
                "actual_end_at",
                sprint.getActualEndAt()
        );

        data.put(
                "duration_weeks",
                sprint.getDurationWeeks()
        );

        data.put(
                "target_velocity",
                sprint.getTargetVelocity()
        );

        data.put(
                "activation_type",
                sprint.getActivationType()
        );

        data.put(
                "created_at",
                sprint.getCreatedAt()
        );

        data.put(
                "updated_at",
                sprint.getUpdatedAt()
        );

        // Angular-friendly aliases
        data.put(
                "projectId",
                sprint.getProject() != null
                        ? sprint.getProject().getId()
                        : null
        );

        data.put(
                "sprintNumber",
                sprint.getSprintNumber()
        );

        data.put(
                "scheduledStartDate",
                sprint.getScheduledStartDate()
        );

        data.put(
                "scheduledEndDate",
                sprint.getScheduledEndDate()
        );

        data.put(
                "durationWeeks",
                sprint.getDurationWeeks()
        );

        data.put(
                "targetVelocity",
                sprint.getTargetVelocity()
        );

        data.put(
                "activationType",
                sprint.getActivationType()
        );

        // There is no is_current column.
        // ACTIVE is the equivalent current state.
        data.put(
                "isCurrent",
                "ACTIVE".equalsIgnoreCase(
                        sprint.getStatus().toString()
                )
        );

        if (sprint.getProject() != null) {
            data.put(
                    "projectName",
                    sprint.getProject().getName()
            );
        }

        return data;
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private UserAccount getAuthenticatedUser(
            String email
    ) {

        if (email == null ||
                email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Authenticated user email is required"
            );
        }

        return userAccountRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        )
                );
    }

    private void validateId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Invalid sprint id"
            );
        }
    }

    private String readString(
            Map<String, Object> data,
            String... keys
    ) {

        for (String key : keys) {

            Object value = data.get(key);

            if (value != null) {
                return value.toString().trim();
            }
        }

        return null;
    }

    private String requiredString(
            Map<String, Object> data,
            String key
    ) {

        String value =
                readString(data, key);

        if (value == null ||
                value.isBlank()) {

            throw new IllegalArgumentException(
                    key + " is required"
            );
        }

        return value;
    }

    private Long readLong(
            Map<String, Object> data,
            String... keys
    ) {

        for (String key : keys) {

            Object value =
                    data.get(key);

            if (value instanceof Number number) {
                return number.longValue();
            }

            if (value != null) {

                try {
                    return Long.parseLong(
                            value.toString().trim()
                    );
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return null;
    }

    private Integer readInteger(
            Map<String, Object> data,
            String... keys
    ) {

        Long value =
                readLong(data, keys);

        return value == null
                ? null
                : value.intValue();
    }

    private Integer defaultInteger(
            Integer value,
            int defaultValue
    ) {

        return value == null
                ? defaultValue
                : value;
    }

    private LocalDate readDate(
            Map<String, Object> data,
            String... keys
    ) {

        String value =
                readString(data, keys);

        if (value == null ||
                value.isBlank()) {

            return null;
        }

        return LocalDate.parse(value);
    }

    private LocalDate requiredDate(
            Map<String, Object> data,
            String... keys
    ) {

        LocalDate value =
                readDate(data, keys);

        if (value == null) {

            throw new IllegalArgumentException(
                    keys[0] + " is required"
            );
        }

        return value;
    }

    private Sprints.Status normalizeStatus(String status, Sprints.Status defaultStatus) {
        if (status == null || status.isBlank()) {
            return defaultStatus;
        }

        return switch (status.trim().toUpperCase()) {
            case "CURRENT" -> Sprints.Status.ACTIVE;
            case "ON_HOLD" -> Sprints.Status.PAUSED;
            case "PLANNED" -> Sprints.Status.PLANNED;
            case "ACTIVE" -> Sprints.Status.ACTIVE;
            case "COMPLETED" -> Sprints.Status.COMPLETED;
            case "PAUSED" -> Sprints.Status.PAUSED;
            default -> defaultStatus;
        };
    }

    private Sprints.ActivationType normalizeActivationType(
            String activationType,
            Sprints.ActivationType defaultType) {

        if (activationType == null || activationType.isBlank()) {
            return defaultType;
        }

        return switch (activationType.trim().toUpperCase()) {
            case "MANUAL" -> Sprints.ActivationType.MANUAL;
            case "AUTOMATIC" -> Sprints.ActivationType.AUTOMATIC;
            default -> defaultType;
        };
    }
}