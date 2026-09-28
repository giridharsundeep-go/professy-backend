package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.ProjectTeamsRepository;
import com.greatleyposhley.professy.repositories.ProjectsRepository;
import com.greatleyposhley.professy.repositories.TeamsRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectTeamsService {

    private final ProjectTeamsRepository projectTeamsRepository;
    private final ProjectsRepository projectsRepository;
    private final TeamsRepository teamsRepository;
    private final UserAccountRepository userAccountRepository;

    public ProjectTeamsService(
            ProjectTeamsRepository projectTeamsRepository,
            ProjectsRepository projectsRepository,
            TeamsRepository teamsRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.projectTeamsRepository = projectTeamsRepository;
        this.projectsRepository = projectsRepository;
        this.teamsRepository = teamsRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public Map<String, Object> syncTeams(
            Long projectId,
            List<Long> teamIds,
            String userEmail
    ) {
        validateProject(projectId);
        Long userId = getUserId(userEmail);

        List<Long> safeTeamIds = teamIds == null ? List.of() : teamIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        projectTeamsRepository.deleteAllForProject(projectId);

        for (Long teamId : safeTeamIds) {
            teamsRepository.findById(teamId)
                    .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));

            projectTeamsRepository.insertProjectTeam(projectId, teamId, userId);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Project team allocations synchronized successfully");
        result.put("project_id", projectId);
        result.put("allocated_teams_count", safeTeamIds.size());
        return result;
    }

    @Transactional(readOnly = true)
    public List<Long> getTeamsByProject(Long projectId, String userEmail) {
        validateProject(projectId);
        getUserId(userEmail);
        return projectTeamsRepository.findTeamIdsByProjectId(projectId);
    }

    @Transactional
    public Map<String, Object> removeTeamFromProject(
            Long projectId,
            Long teamId,
            String userEmail
    ) {
        validateProject(projectId);
        getUserId(userEmail);

        int deleted = projectTeamsRepository.removeProjectTeam(projectId, teamId);
        if (deleted == 0) {
            throw new ProjectsService.ResourceNotFoundException("Allocation mapping not found");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Team removed from project successfully");
        result.put("project_id", projectId);
        result.put("team_id", teamId);
        return result;
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
