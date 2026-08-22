package com.greatleyposhley.professy.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "issue_allocations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_issue_allocatable",
                columnNames = {"issue_id", "allocatable_type", "allocatable_id"}
        )
)
public class IssueAllocations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    @JsonBackReference
    private Issues issue;

    @Enumerated(EnumType.STRING)
    @Column(name = "allocatable_type", nullable = false, length = 50)
    private AllocatableType allocatableType;

    @Column(name = "allocatable_id", nullable = false)
    private Long allocatableId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public IssueAllocations() {}

    public IssueAllocations(AllocatableType allocatableType, Long allocatableId) {
        this.allocatableType = allocatableType;
        this.allocatableId = allocatableId;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // --- GETTERS & SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Issues getIssue() {
        return issue;
    }

    public void setIssue(Issues issue) {
        this.issue = issue;
    }

    public AllocatableType getAllocatableType() {
        return allocatableType;
    }

    public void setAllocatableType(AllocatableType allocatableType) {
        this.allocatableType = allocatableType;
    }

    public Long getAllocatableId() {
        return allocatableId;
    }

    public void setAllocatableId(Long allocatableId) {
        this.allocatableId = allocatableId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}