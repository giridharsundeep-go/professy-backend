package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_event_outbox", indexes = {@Index(name = "ix_workspace_events", columnList = "published_at,id")})
@Entity
public class WorkspaceEventOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "event_id", nullable = false, columnDefinition = "CHAR(36)", unique = true)
    private String eventId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "event_type", nullable = false, columnDefinition = "VARCHAR(50)")
    private String eventType;
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long userId;
    @Column(name = "published_at", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime publishedAt;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected WorkspaceEventOutbox() {}
}
