package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_meetings", indexes = {@Index(name = "ix_meetings", columnList = "org_id,owner_id")})
@Entity
public class WorkspaceMeetings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;
    @Column(name = "owner_id", nullable = false, columnDefinition = "BIGINT")
    private Long ownerId;
    @Column(name = "starts_at", nullable = true, columnDefinition = "DATETIME")
    private java.time.LocalDateTime startsAt;
    @Column(name = "ends_at", nullable = true, columnDefinition = "DATETIME")
    private java.time.LocalDateTime endsAt;
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'SCHEDULED'")
    private String status;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected WorkspaceMeetings() {}
}
