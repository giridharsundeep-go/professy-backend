package com.greatleyposhley.professy.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doc_api_versions")
public class DocApiVersion {

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

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "version_label", nullable = false, length = 50)
    private String versionLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "specification_format", nullable = false)
    private SpecificationFormat specificationFormat =
            SpecificationFormat.OPENAPI_YAML;

    @Column(columnDefinition = "LONGTEXT")
    private String specification;

    @Column(name = "specification_sha256", length = 64)
    private String specificationSha256;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiVersionStatus status = ApiVersionStatus.DRAFT;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(columnDefinition = "TEXT")
    private String changelog;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

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

    public void setUserId(UserAccount userId) {
        this.userAccount = userId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getVersionLabel() {
        return versionLabel;
    }

    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    public SpecificationFormat getSpecificationFormat() {
        return specificationFormat;
    }

    public void setSpecificationFormat(SpecificationFormat specificationFormat) {
        this.specificationFormat = specificationFormat;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getSpecificationSha256() {
        return specificationSha256;
    }

    public void setSpecificationSha256(String specificationSha256) {
        this.specificationSha256 = specificationSha256;
    }

    public ApiVersionStatus getStatus() {
        return status;
    }

    public void setStatus(ApiVersionStatus status) {
        this.status = status;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
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

    public enum SpecificationFormat { OPENAPI_JSON, OPENAPI_YAML }
    public enum ApiVersionStatus { DRAFT, PUBLISHED, DEPRECATED, ARCHIVED }
}