package com.greatleyposhley.professy.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "testcases")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Testcases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_case_code", length = 50, unique = true)
    private String testCaseCode;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "preconditions", columnDefinition = "TEXT")
    private String preconditions;

    @Column(name = "steps", nullable = false, columnDefinition = "TEXT")
    private String steps;

    @Column(name = "expected_result", nullable = false, columnDefinition = "TEXT")
    private String expectedResult;

    @Column(name = "actual_result", columnDefinition = "TEXT")
    private String actualResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private TestCaseStatus status = TestCaseStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 50, nullable = false)
    private TestCasePriority priority = TestCasePriority.MEDIUM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnoreProperties({"testcases", "product", "userAccount", "hibernateLazyInitializer", "handler"})
    private Projects project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epic_id")
    @JsonIgnoreProperties({"assignee", "reporter", "creator", "userAccount", "sprint", "project", "testcases", "hibernateLazyInitializer", "handler"})
    private Epics epic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    @JsonIgnoreProperties({"assignee", "creator", "reporter", "project", "epic", "testcases", "hibernateLazyInitializer", "handler"})
    private Stories story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    @JsonIgnoreProperties({"assignee", "creator", "reporter", "project", "epic", "story", "testcases", "hibernateLazyInitializer", "handler"})
    private Tasks task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"manager", "role", "userAccount", "hibernateLazyInitializer", "handler"})
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({"manager", "role", "userAccount", "hibernateLazyInitializer", "handler"})
    private Users creator;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Testcases() {
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTestCaseCode() { return testCaseCode; }
    public void setTestCaseCode(String testCaseCode) { this.testCaseCode = testCaseCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPreconditions() { return preconditions; }
    public void setPreconditions(String preconditions) { this.preconditions = preconditions; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }

    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }

    public String getActualResult() { return actualResult; }
    public void setActualResult(String actualResult) { this.actualResult = actualResult; }

    public TestCaseStatus getStatus() { return status; }
    public void setStatus(TestCaseStatus status) { this.status = status; }

    public TestCasePriority getPriority() { return priority; }
    public void setPriority(TestCasePriority priority) { this.priority = priority; }

    public Projects getProject() { return project; }
    public void setProject(Projects project) { this.project = project; }

    public Epics getEpic() { return epic; }
    public void setEpic(Epics epic) { this.epic = epic; }

    public Stories getStory() { return story; }
    public void setStory(Stories story) { this.story = story; }

    public Tasks getTask() { return task; }
    public void setTask(Tasks task) { this.task = task; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public Users getCreator() { return creator; }
    public void setCreator(Users creator) { this.creator = creator; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}