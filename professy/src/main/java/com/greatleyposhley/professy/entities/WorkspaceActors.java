package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_actors", indexes = {@Index(name = "ix_actor_org", columnList = "org_id,actor_type")})
@Entity
public class WorkspaceActors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "actor_type", nullable = false, columnDefinition = "VARCHAR(10)")
    private String actorType;
    @Column(name = "display_name", nullable = false, columnDefinition = "VARCHAR(120)")
    private String displayName;
    @Column(name = "user_id", nullable = true, columnDefinition = "BIGINT")
    private Long userId;
    @Column(name = "provider", nullable = true, columnDefinition = "VARCHAR(100)")
    private String provider;
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'DRAFT'")
    private String status;
    @Column(name = "created_by", nullable = false, columnDefinition = "BIGINT")
    private Long createdBy;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected WorkspaceActors() {}
}
