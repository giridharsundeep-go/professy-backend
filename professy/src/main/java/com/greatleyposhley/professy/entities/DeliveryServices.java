package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "delivery_services", indexes = {@Index(name = "services_project", columnList = "org_id,project_id")}, uniqueConstraints = {@UniqueConstraint(name = "service_name", columnNames = {"org_id", "project_id", "name"})})
@Entity
public class DeliveryServices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(160)")
    private String name;
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "kind", nullable = false, columnDefinition = "VARCHAR(30)")
    private String kind;
    @Column(name = "environment", nullable = false, columnDefinition = "VARCHAR(40)")
    private String environment;
    @Column(name = "repository_url", nullable = false, columnDefinition = "VARCHAR(500) DEFAULT ''")
    private String repositoryUrl;
    @Column(name = "owner_id", nullable = true, columnDefinition = "BIGINT")
    private Long ownerId;
    @Column(name = "owner_type", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'HUMAN'")
    private String ownerType;
    @Column(name = "port", nullable = true, columnDefinition = "INT")
    private Integer port;
    @Column(name = "created_by", nullable = false, columnDefinition = "BIGINT")
    private Long createdBy;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    protected DeliveryServices() {}
}
