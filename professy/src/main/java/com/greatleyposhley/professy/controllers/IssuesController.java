package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.Issues;
import com.greatleyposhley.professy.services.IssuesService;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
@CrossOrigin(origins = "http://localhost:4200")
public class IssuesController {

    private final IssuesService issuesService;
    private final UserAccountRepository userAccountRepository;

    public IssuesController(IssuesService issuesService,
                            UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        this.issuesService = issuesService;
    }

    @PostMapping
    public ResponseEntity<Issues> createIssue(@RequestBody Issues issue,
                                              Authentication authentication) {
        Long orgId = getOrgId(authentication);
        Issues createdIssue = issuesService.createIssue(orgId,
                issue);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIssue);
    }

    @PostMapping("/bulk-create")
    public ResponseEntity<List<Issues>> bulkCreateIssues(@RequestBody List<Issues> issues,
                                                         Authentication authentication) {
        Long orgId = getOrgId(authentication);
        List<Issues> createdIssues = issuesService.bulkCreateIssues(orgId,
                issues);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIssues);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Issues> updateIssue(@PathVariable("id") long id, @RequestBody Issues issue,
                                              Authentication authentication) {
        Long orgId = getOrgId(authentication);
        Issues updatedIssue = issuesService.updateIssue(orgId,
                id, issue);
        return ResponseEntity.ok(updatedIssue);
    }

    @GetMapping
    public ResponseEntity<List<Issues>> getAllIssues(@RequestParam(required = false) Long projectId,
                                                     @RequestParam(required = false) Long sprintId,
                                                     @RequestParam(required = false) Long assigneeId,
                                                     @RequestParam(required = false) Long reporterId,
                                                     @RequestParam(required = false) String status,
                                                     Authentication authentication) {
        Long orgId = getOrgId(authentication);

        if (projectId != null && sprintId != null) {
            return ResponseEntity.ok(issuesService.getIssuesByProjectAndSprint(orgId,
                    projectId, sprintId));
        }
        if (projectId != null) {
            return ResponseEntity.ok(issuesService.getIssuesByProjectId(orgId,
                    projectId));
        }
        if (sprintId != null) {
            return ResponseEntity.ok(issuesService.getIssuesBySprintId(orgId,
                    sprintId));
        }
        if (assigneeId != null) {
            return ResponseEntity.ok(issuesService.getIssuesByAssigneeId(orgId,
                    assigneeId));
        }
        if (reporterId != null) {
            return ResponseEntity.ok(issuesService.getIssuesByReporterId(orgId,
                    reporterId));
        }
        if (status != null) {
            return ResponseEntity.ok(issuesService.getIssuesByStatus(orgId,
                    status));
        }

        return ResponseEntity.ok(issuesService.getAllIssues(orgId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Issues> getIssueById(@PathVariable Long id,
                                               Authentication authentication) {
        Long orgId = getOrgId(authentication);
        return ResponseEntity.ok(issuesService.getIssueById(orgId,
                id));
    }

    @GetMapping("/code/{issueCode}")
    public ResponseEntity<Issues> getIssueByCode(@PathVariable String issueCode,
                                                 Authentication authentication) {
        Long orgId = getOrgId(authentication);
        return ResponseEntity.ok(issuesService.getIssueByCode(orgId,
                issueCode));
    }

    @GetMapping("/epic/{epicId}")
    public ResponseEntity<List<Issues>> getIssuesByEpicId(@PathVariable Long epicId,
                                                          Authentication authentication) {
        Long orgId = getOrgId(authentication);
        return ResponseEntity.ok(issuesService.getIssuesByEpicId(orgId,
                epicId));
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<Issues>> getIssuesByStoryId(@PathVariable Long storyId,
                                                           Authentication authentication) {
        Long orgId = getOrgId(authentication);
        return ResponseEntity.ok(issuesService.getIssuesByStoryId(orgId,
                storyId));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Issues>> getIssuesByTaskId(@PathVariable Long taskId,
                                                          Authentication authentication) {
        Long orgId = getOrgId(authentication);
        return ResponseEntity.ok(issuesService.getIssuesByTaskId(orgId,
                taskId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id,
                                            Authentication authentication) {
        Long orgId = getOrgId(authentication);
        issuesService.deleteIssue(orgId,
                id);
        return ResponseEntity.noContent().build();
    }


    private Long getOrgId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentication required");
        }

        String email = authentication.getName();

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Authenticated user is required");
        }

        return userAccountRepository
                .findByEmail(email.trim().toLowerCase())
                .map(account -> account.getOrgId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Authenticated user not found")
                );
    }
}
