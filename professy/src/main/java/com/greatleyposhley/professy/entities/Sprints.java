package com.greatleyposhley.professy.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "sprints",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_project_sprint_order", columnNames = {"project_id", "sprint_number"})
        }
)
public class Sprints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key to projects table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Projects project;

    // Foreign Key to user_account table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "sprint_number", nullable = false)
    private Integer sprintNumber;

    @Column(nullable = false, length = 191)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PLANNED;

    @Column(name = "scheduled_start_date", nullable = false)
    private LocalDate scheduledStartDate;

    @Column(name = "scheduled_end_date", nullable = false)
    private LocalDate scheduledEndDate;

    @Column(name = "actual_start_at")
    private LocalDateTime actualStartAt;

    @Column(name = "actual_end_at")
    private LocalDateTime actualEndAt;

    @Column(name = "duration_weeks", nullable = false)
    private Integer durationWeeks = 2;

    @Column(name = "target_velocity", nullable = false)
    private Integer targetVelocity = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "activation_type", nullable = false)
    private ActivationType activationType = ActivationType.AUTOMATIC;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- Enums ---

    public enum Status {
        PLANNED,
        ACTIVE,
        COMPLETED,
        PAUSED
    }

    public enum ActivationType {
        MANUAL,
        AUTOMATIC
    }

    // --- Constructors ---

    public Sprints() {
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Projects getProject() {
        return project;
    }

    public void setProject(Projects project) {
        this.project = project;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Integer getSprintNumber() {
        return sprintNumber;
    }

    public void setSprintNumber(Integer sprintNumber) {
        this.sprintNumber = sprintNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getScheduledStartDate() {
        return scheduledStartDate;
    }

    public void setScheduledStartDate(LocalDate scheduledStartDate) {
        this.scheduledStartDate = scheduledStartDate;
    }

    public LocalDate getScheduledEndDate() {
        return scheduledEndDate;
    }

    public void setScheduledEndDate(LocalDate scheduledEndDate) {
        this.scheduledEndDate = scheduledEndDate;
    }

    public LocalDateTime getActualStartAt() {
        return actualStartAt;
    }

    public void setActualStartAt(LocalDateTime actualStartAt) {
        this.actualStartAt = actualStartAt;
    }

    public LocalDateTime getActualEndAt() {
        return actualEndAt;
    }

    public void setActualEndAt(LocalDateTime actualEndAt) {
        this.actualEndAt = actualEndAt;
    }

    public Integer getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(Integer durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public Integer getTargetVelocity() {
        return targetVelocity;
    }

    public void setTargetVelocity(Integer targetVelocity) {
        this.targetVelocity = targetVelocity;
    }

    public ActivationType getActivationType() {
        return activationType;
    }

    public void setActivationType(ActivationType activationType) {
        this.activationType = activationType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}