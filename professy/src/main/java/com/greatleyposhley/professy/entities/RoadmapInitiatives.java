package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "roadmap_initiatives", indexes = {@Index(name = "ix_roadmap_project", columnList = "org_id,project_id,starts_on")})
@Entity
public class RoadmapInitiatives {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;
    @Column(name = "description", nullable = true, columnDefinition = "TEXT")
    private String description;
    @Column(name = "owner_id", nullable = true, columnDefinition = "BIGINT")
    private Long ownerId;
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PLANNED'")
    private String status;
    @Column(name = "starts_on", nullable = false, columnDefinition = "DATE")
    private java.time.LocalDate startsOn;
    @Column(name = "target_on", nullable = false, columnDefinition = "DATE")
    private java.time.LocalDate targetOn;
    @Column(name = "ticket_refs", nullable = false, columnDefinition = "JSON")
    private String ticketRefs;
    @Column(name = "created_by", nullable = false, columnDefinition = "BIGINT")
    private Long createdBy;
    @Column(name = "revision", nullable = false, columnDefinition = "BIGINT DEFAULT 1")
    private Long revision;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    protected RoadmapInitiatives() {}
}
