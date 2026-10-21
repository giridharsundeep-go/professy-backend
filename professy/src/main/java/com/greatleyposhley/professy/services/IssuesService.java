package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class IssuesService {

    @Autowired
    SprintsRepository sprintsRepository;

    @Autowired
    ProjectsRepository projectsRepository;

    @Autowired
    IssuesRepository issuesRepository;

    @Autowired
    UserAccountRepository usersRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    IssueAllocationsRepository issueAllocationsRepository;

    @Autowired
    EntityManager entityManager;

    public IssuesService(IssuesRepository issuesRepository) {
        this.issuesRepository = issuesRepository;
    }

    @Transactional
    public Issues createIssue(Long orgId, Issues issue) {
        if (issue == null) {
            throw new IllegalArgumentException("Issue is required");
        }
        issue.setOrgId(orgId);
        // 1. Resolve Project
        if (issue.getProject() != null && issue.getProject().getId() != null) {
            Long projId = issue.getProject().getId();
            Projects project = projectsRepository.findById(projId)
                    .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projId));
            issue.setProject(project);
        } else {
            throw new IllegalArgumentException("Project must be provided with a valid ID.");
        }

        // 2. Resolve User (user_id column)
        if (issue.getUser() != null && issue.getUser().getId() != null) {
            Long userId = issue.getUser().getId();
            UserAccount user = userAccountRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
            issue.setUser(user);
        } else if (issue.getCreator() != null && extractId(issue.getCreator()) != null) {
            Long creatorId = extractId(issue.getCreator());
            UserAccount user = userAccountRepository.findById(creatorId)
                    .orElseThrow(() -> new EntityNotFoundException("User entity not found with ID: " + creatorId));
            issue.setUser(user);
        } else {
            throw new IllegalArgumentException("User (user_id) must be provided with a valid ID.");
        }

        // 3. Resolve Polymorphic Creator (Users vs UserAccount)
        Object creatorSource = issue.getCreator();

        if (creatorSource != null) {
            Long creatorId = extractId(creatorSource);

            if (creatorId == null) {
                throw new IllegalArgumentException("Creator ID could not be determined from input: " + creatorSource);
            }

            Optional<UserAccount> userOpt = usersRepository.findById(creatorId);
            Optional<UserAccount> adminOpt = userAccountRepository.findById(creatorId);

            if (userOpt.isPresent()) {
                issue.setCreator(userOpt.get()); // Attaches Users entity
            } else if (adminOpt.isPresent()) {
                issue.setCreator(adminOpt.get()); // Attaches UserAccount entity
            } else {
                throw new EntityNotFoundException("Creator entity not found with ID: " + creatorId);
            }

            // Line removed: issue.setCreator(creatorId); was overwriting the Entity with Long
        } else {
            throw new IllegalArgumentException("Creator must be provided with a valid User ID.");
        }

        // 4. Resolve Sprint
        if (issue.getSprint() != null && issue.getSprint().getId() != null) {
            Long sprintId = issue.getSprint().getId();
            Sprints sprint = sprintsRepository.findById(sprintId).orElse(null);
            issue.setSprint(sprint);
        } else {
            issue.setSprint(null);
        }

        // 5. Resolve Assignee
        if (issue.getAssignee() != null && issue.getAssignee().getId() != null) {
            Long assigneeId = issue.getAssignee().getId();
            UserAccount assignee = usersRepository.findById(assigneeId).orElse(null);
            issue.setAssignee(assignee);
        } else {
            issue.setAssignee(null);
        }

        // 6. Resolve Reporter
        if (issue.getReporter() != null && issue.getReporter().getId() != null) {
            Long reporterId = issue.getReporter().getId();
            UserAccount reporter = usersRepository.findById(reporterId).orElse(null);
            issue.setReporter(reporter);
        } else {
            issue.setReporter(null);
        }

        if (issue.getAllocations() != null) {
            for (IssueAllocations allocation : issue.getAllocations()) {
                allocation.setIssue(issue);
            }
        }

        return issuesRepository.save(issue);
    }

    @Transactional
    public List<Issues> bulkCreateIssues(Long orgId, List<Issues> issues) {

        if (issues == null || issues.isEmpty()) {
            throw new IllegalArgumentException("At least one issue is required");
        }

        List<Issues> createdIssues = new java.util.ArrayList<>();

        for (int i = 0; i < issues.size(); i++) {
            Issues issue = issues.get(i);

            if (issue == null) {
                throw new IllegalArgumentException(
                        "Issue at index " + i + " must not be null"
                );
            }

            createdIssues.add(createIssue(orgId, issue));
        }

        return createdIssues;
    }

    private Long extractId(Object obj) {
        if (obj == null) return null;

        if (obj instanceof Number number) {
            return number.longValue();
        }
        if (obj instanceof String str) {
            try { return Long.parseLong(str); } catch (NumberFormatException e) { return null; }
        }
        if (obj instanceof Map<?, ?> map) {
            Object idVal = map.get("id");
            return extractId(idVal);
        }
        if (obj instanceof UserAccount user) {
            return user.getId();
        }
        if (obj instanceof UserAccount account) {
            return account.getId();
        }

        return null;
    }

    public List<Issues> getIssuesByProjectAndSprint(Long orgId, Long projectId, Long sprintId) {
        if (projectId == null || sprintId == null) {
            return List.of();
        }
        return issuesRepository.findByOrgIdAndProjectIdAndSprintId(orgId, projectId, sprintId);
    }

    @Transactional(readOnly = true)
    public List<Issues> getAllIssues(Long orgId) {
        return issuesRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Issues getIssueById(Long orgId, Long id) {
        return issuesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Issues getIssueByCode(Long orgId, String issueCode) {
        return issuesRepository.findByOrgIdAndIssueCode(orgId, issueCode)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with code: " + issueCode));
    }

    @Transactional(readOnly = true)
    public List<Issues> getIssuesByProjectId(Long orgId, Long projectId) {
        return issuesRepository.findByOrgIdAndProjectId(orgId, projectId);
    }

    @Transactional(readOnly = true)
    public List<Issues> getIssuesBySprintId(Long orgId, Long sprintId) {
        return issuesRepository.findByOrgIdAndSprintId(orgId, sprintId);
    }

    @Transactional(readOnly = true)
    public List<Issues> getIssuesByAssigneeId(Long orgId, Long assigneeId) {
        return issuesRepository.findByOrgIdAndAssigneeId(orgId, assigneeId);
    }

    @Transactional(readOnly = true)
    public List<Issues> getIssuesByReporterId(Long orgId, Long reporterId) {
        return issuesRepository.findByOrgIdAndReporterId(orgId, reporterId);
    }

    @Transactional(readOnly = true)
    public List<Issues> getIssuesByStatus(Long orgId, String status) {
        return issuesRepository.findByOrgIdAndStatus(orgId, status);
    }

    @Transactional(readOnly = true)
    public List<Issues> getIssuesByEpicId(Long orgId, Long epicId) {
        return issuesRepository.findByEpicId(orgId, epicId);
    }

    @Transactional(readOnly = true)
    public List<Issues> getIssuesByStoryId(Long orgId, Long storyId) {
        return issuesRepository.findByStoryId(orgId, storyId);
    }

    @Transactional(readOnly = true)
    public List<Issues> getIssuesByTaskId(Long orgId, Long taskId) {
        return issuesRepository.findByTaskId(orgId, taskId);
    }

    @Transactional
    public Issues updateIssue(Long orgId, Long id, Issues updatedIssue) {
        Issues existingIssue = issuesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with ID: " + id));

        existingIssue.setIssueCode(updatedIssue.getIssueCode());
        existingIssue.setTitle(updatedIssue.getTitle());
        existingIssue.setDescription(updatedIssue.getDescription());
        existingIssue.setStatus(updatedIssue.getStatus());

        existingIssue.setProject(
                (updatedIssue.getProject() != null && updatedIssue.getProject().getId() != null)
                        ? projectsRepository.findById(updatedIssue.getProject().getId()).orElse(null) : null
        );

        existingIssue.setSprint(
                (updatedIssue.getSprint() != null && updatedIssue.getSprint().getId() != null)
                        ? sprintsRepository.findById(updatedIssue.getSprint().getId()).orElse(null) : null
        );

        existingIssue.setAssignee(
                (updatedIssue.getAssignee() != null && updatedIssue.getAssignee().getId() != null)
                        ? usersRepository.findById(updatedIssue.getAssignee().getId()).orElse(null) : null
        );

        existingIssue.setReporter(
                (updatedIssue.getReporter() != null && updatedIssue.getReporter().getId() != null)
                        ? usersRepository.findById(updatedIssue.getReporter().getId()).orElse(null) : null
        );

        // 1. Clear memory collection (relies on orphanRemoval = true on @OneToMany)
        existingIssue.getAllocations().clear();

        // 2. Force Hibernate to execute SQL DELETEs now to avoid unique key conflicts
        entityManager.flush();

        // 3. Add new allocations
        if (updatedIssue.getAllocations() != null) {
            for (IssueAllocations incomingAlloc : updatedIssue.getAllocations()) {
                IssueAllocations alloc = new IssueAllocations();
                alloc.setAllocatableType(incomingAlloc.getAllocatableType());
                alloc.setAllocatableId(incomingAlloc.getAllocatableId());
                existingIssue.addAllocation(alloc);
            }
        }

        return issuesRepository.save(existingIssue);
    }

    public void deleteIssue(Long orgId, Long id) {
        if (!issuesRepository.existsById(id)) {
            throw new EntityNotFoundException("Issue not found with ID: " + id);
        }
        issuesRepository.deleteById(id);
    }
}