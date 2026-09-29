package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.Issues;
import com.greatleyposhley.professy.services.IssuesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
@CrossOrigin(origins = "http://localhost:4200")
public class IssuesController {

    private final IssuesService issuesService;

    public IssuesController(IssuesService issuesService) {
        this.issuesService = issuesService;
    }

    @PostMapping
    public ResponseEntity<Issues> createIssue(@RequestBody Issues issue) {
        Issues createdIssue = issuesService.createIssue(issue);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIssue);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Issues> updateIssue(@PathVariable("id") long id, @RequestBody Issues issue) {
        Issues updatedIssue = issuesService.updateIssue(id, issue);
        return ResponseEntity.ok(updatedIssue);
    }

    @GetMapping
    public ResponseEntity<List<Issues>> getAllIssues(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long sprintId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long reporterId,
            @RequestParam(required = false) String status) {

        if (projectId != null && sprintId != null) {
            return ResponseEntity.ok(issuesService.getIssuesByProjectAndSprint(projectId, sprintId));
        }
        if (projectId != null) {
            return ResponseEntity.ok(issuesService.getIssuesByProjectId(projectId));
        }
        if (sprintId != null) {
            return ResponseEntity.ok(issuesService.getIssuesBySprintId(sprintId));
        }
        if (assigneeId != null) {
            return ResponseEntity.ok(issuesService.getIssuesByAssigneeId(assigneeId));
        }
        if (reporterId != null) {
            return ResponseEntity.ok(issuesService.getIssuesByReporterId(reporterId));
        }
        if (status != null) {
            return ResponseEntity.ok(issuesService.getIssuesByStatus(status));
        }

        return ResponseEntity.ok(issuesService.getAllIssues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Issues> getIssueById(@PathVariable Long id) {
        return ResponseEntity.ok(issuesService.getIssueById(id));
    }

    @GetMapping("/code/{issueCode}")
    public ResponseEntity<Issues> getIssueByCode(@PathVariable String issueCode) {
        return ResponseEntity.ok(issuesService.getIssueByCode(issueCode));
    }

    @GetMapping("/epic/{epicId}")
    public ResponseEntity<List<Issues>> getIssuesByEpicId(@PathVariable Long epicId) {
        return ResponseEntity.ok(issuesService.getIssuesByEpicId(epicId));
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<Issues>> getIssuesByStoryId(@PathVariable Long storyId) {
        return ResponseEntity.ok(issuesService.getIssuesByStoryId(storyId));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Issues>> getIssuesByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(issuesService.getIssuesByTaskId(taskId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        issuesService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }
}
