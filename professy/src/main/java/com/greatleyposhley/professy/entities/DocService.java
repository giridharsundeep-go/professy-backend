package com.greatleyposhley.professy.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserAccount userAccount;

    @Transient
    @JsonProperty("user_id")
    private Long requestedUserId;

    public Long getRequestedUserId() {
        return requestedUserId;
    }

    public void setRequestedUserId(Long requestedUserId) {
        this.requestedUserId = requestedUserId;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public UserAccount getUserId() {
        return userAccount;
    }

    public void setUserId(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getBaseUrlDev() {
        return baseUrlDev;
    }

    public void setBaseUrlDev(String baseUrlDev) {
        this.baseUrlDev = baseUrlDev;
    }

    public String getBaseUrlTest() {
        return baseUrlTest;
    }

    public void setBaseUrlTest(String baseUrlTest) {
        this.baseUrlTest = baseUrlTest;
    }

    public String getBaseUrlProd() {
        return baseUrlProd;
    }

    public void setBaseUrlProd(String baseUrlProd) {
        this.baseUrlProd = baseUrlProd;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
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

    public enum ServiceType {
        REST_API, GRAPHQL, EVENTS, SDK, INTERNAL_SERVICE, OTHER
    }
    public enum ServiceStatus { ACTIVE, DEPRECATED, ARCHIVED }
}