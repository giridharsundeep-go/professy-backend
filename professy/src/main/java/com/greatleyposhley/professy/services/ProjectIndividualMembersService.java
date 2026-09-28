package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.ProjectIndividualMembersRepository;
import com.greatleyposhley.professy.repositories.ProjectsRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectIndividualMembersService {

    private final ProjectIndividualMembersRepository repository;
    private final ProjectsRepository projectsRepository;
    private final UserAccountRepository userAccountRepository;

    public ProjectIndividualMembersService(
            ProjectIndividualMembersRepository repository,
            ProjectsRepository projectsRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.repository = repository;
        this.projectsRepository = projectsRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public Map<String, Object> syncIndividuals(
            Long projectId,
            List<Long> userAccountIds,
            String userEmail
    ) {
        validateProject(projectId);
        Long trackingUserId = getUserId(userEmail);

        List<Long> safeIds = userAccountIds == null ? List.of() : userAccountIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        repository.deleteAllForProject(projectId);

        for (Long userAccountId : safeIds) {
            userAccountRepository.findById(userAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("User account not found: " + userAccountId));

            repository.insertProjectIndividual(projectId, userAccountId, trackingUserId);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Project individual member allocations synchronized successfully");
        result.put("project_id", projectId);
        result.put("allocated_individuals_count", safeIds.size());
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getIndividualsByProject(
            Long projectId,
            String userEmail
    ) {
        validateProject(projectId);
        getUserId(userEmail);

        List<Long> ids = repository.findUserAccountIdsByProjectId(projectId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Long id : ids) {
            UserAccount user = userAccountRepository.findById(id).orElse(null);
            if (user == null) {
                continue;
            }

            Map<String, Object> userData = new LinkedHashMap<>();
            userData.put("id", user.getId());
            userData.put("name", buildName(user));
            userData.put("email", user.getEmail());
            userData.put("role", "TEAM_MEMBER");
            result.add(userData);
        }

        return result;
    }

    @Transactional
    public Map<String, Object> removeIndividual(
            Long projectId,
            Long userAccountId,
            String userEmail
    ) {
        validateProject(projectId);
        getUserId(userEmail);

        int deleted = repository.removeProjectIndividual(projectId, userAccountId);
        if (deleted == 0) {
            throw new ProjectsService.ResourceNotFoundException("Allocation mapping not found");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Individual specialist removed from project successfully");
        result.put("project_id", projectId);
        result.put("user_account_id", userAccountId);
        return result;
    }

    private String buildName(UserAccount user) {
        String first = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();
        String full = (first + " " + last).trim();
        return full.isEmpty() ? user.getEmail() : full;
    }

    private void validateProject(Long projectId) {
        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException("project_id is required");
        }
        if (projectsRepository.findProjectRowById(projectId) == null) {
            throw new ProjectsService.ResourceNotFoundException("Project not found");
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
}
