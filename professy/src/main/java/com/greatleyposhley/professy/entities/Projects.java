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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key to user_account table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    // Optional Foreign Key to products table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Products product;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String status = "ACTIVE";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Methodology methodology = Methodology.AGILE_SCRUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "total_backlog_points")
    private Integer totalBacklogPoints = 0;

    @Column(name = "sprint_duration_weeks")
    private Integer sprintDurationWeeks;

    @Column(name = "target_velocity")
    private Integer targetVelocity;

    @Column(name = "auto_rollover_backlog")
    private Boolean autoRolloverBacklog = true;

    @Column(name = "computed_sprint_count")
    private Integer computedSprintCount;

    @Column(name = "computed_total_duration_weeks")
    private Integer computedTotalDurationWeeks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "project_code", length = 100)
    private String projectCode;

    // --- Enums ---

    public enum Methodology {
        AGILE_SCRUM,
        WATERFALL_GANTT
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    // --- Constructors ---

    public Projects() {
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Methodology getMethodology() {
        return methodology;
    }

    public void setMethodology(Methodology methodology) {
        this.methodology = methodology;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Integer getTotalBacklogPoints() {
        return totalBacklogPoints;
    }

    public void setTotalBacklogPoints(Integer totalBacklogPoints) {
        this.totalBacklogPoints = totalBacklogPoints;
    }

    public Integer getSprintDurationWeeks() {
        return sprintDurationWeeks;
    }

    public void setSprintDurationWeeks(Integer sprintDurationWeeks) {
        this.sprintDurationWeeks = sprintDurationWeeks;
    }

    public Integer getTargetVelocity() {
        return targetVelocity;
    }

    public void setTargetVelocity(Integer targetVelocity) {
        this.targetVelocity = targetVelocity;
    }

    public Boolean getAutoRolloverBacklog() {
        return autoRolloverBacklog;
    }

    public void setAutoRolloverBacklog(Boolean autoRolloverBacklog) {
        this.autoRolloverBacklog = autoRolloverBacklog;
    }

    public Integer getComputedSprintCount() {
        return computedSprintCount;
    }

    public void setComputedSprintCount(Integer computedSprintCount) {
        this.computedSprintCount = computedSprintCount;
    }

    public Integer getComputedTotalDurationWeeks() {
        return computedTotalDurationWeeks;
    }

    public void setComputedTotalDurationWeeks(Integer computedTotalDurationWeeks) {
        this.computedTotalDurationWeeks = computedTotalDurationWeeks;
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

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }
}