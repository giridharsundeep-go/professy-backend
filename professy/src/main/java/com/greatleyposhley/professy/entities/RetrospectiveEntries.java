package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "retrospective_entries", indexes = {@Index(name = "retro_scope", columnList = "org_id,sprint_id")})
@Entity
public class RetrospectiveEntries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "sprint_id", nullable = false, columnDefinition = "BIGINT")
    private Long sprintId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "category", nullable = false, columnDefinition = "VARCHAR(20)")
    private String category;
    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;
    @Column(name = "author_id", nullable = false, columnDefinition = "BIGINT")
    private Long authorId;
    @Column(name = "owner_id", nullable = true, columnDefinition = "BIGINT")
    private Long ownerId;
    @Column(name = "due_date", nullable = true, columnDefinition = "DATE")
    private java.time.LocalDate dueDate;
    @Column(name = "action_issue_id", nullable = true, columnDefinition = "BIGINT")
    private Long actionIssueId;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected RetrospectiveEntries() {}
}
