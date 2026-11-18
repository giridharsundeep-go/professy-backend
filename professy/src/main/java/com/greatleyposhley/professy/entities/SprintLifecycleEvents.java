package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "sprint_lifecycle_events", indexes = {@Index(name = "idx_sprint_lifecycle", columnList = "sprint_id,id")})
@Entity
public class SprintLifecycleEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Column(name = "sprint_id", nullable = false, columnDefinition = "BIGINT")
    private Long sprintId;
    @Column(name = "actor_id", nullable = false, columnDefinition = "BIGINT")
    private Long actorId;
    @Column(name = "event_type", nullable = false, columnDefinition = "VARCHAR(40)")
    private String eventType;
    @Column(name = "details", nullable = false, columnDefinition = "JSON")
    private String details;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected SprintLifecycleEvents() {}
}
