package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doc_services")
public class DocService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_id", nullable = false)
    private Long orgId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 220)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType = ServiceType.REST_API;

    @Column(name = "repository_url", length = 1000)
    private String repositoryUrl;

    @Column(name = "base_url_dev", length = 1000)
    private String baseUrlDev;

    @Column(name = "base_url_test", length = 1000)
    private String baseUrlTest;

    @Column(name = "base_url_prod", length = 1000)
    private String baseUrlProd;

    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceStatus status = ServiceStatus.ACTIVE;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public enum ServiceType {
        REST_API, GRAPHQL, EVENTS, SDK, INTERNAL_SERVICE, OTHER
    }
    public enum ServiceStatus { ACTIVE, DEPRECATED, ARCHIVED }
}