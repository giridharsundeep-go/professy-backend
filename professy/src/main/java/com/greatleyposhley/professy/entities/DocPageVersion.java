package com.greatleyposhley.professy.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doc_page_versions")
public class DocPageVersion {

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

    @Column(name = "page_id", nullable = false)
    private Long pageId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_format", nullable = false)
    private DocPage.ContentFormat contentFormat =
            DocPage.ContentFormat.MARKDOWN;

    @Column(name = "change_summary", length = 1000)
    private String changeSummary;

    @Enumerated(EnumType.STRING)
    @Column(name = "version_status", nullable = false)
    private VersionStatus versionStatus = VersionStatus.DRAFT;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

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

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DocPage.ContentFormat getContentFormat() {
        return contentFormat;
    }

    public void setContentFormat(DocPage.ContentFormat contentFormat) {
        this.contentFormat = contentFormat;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }

    public VersionStatus getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(VersionStatus versionStatus) {
        this.versionStatus = versionStatus;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public enum VersionStatus { DRAFT, PUBLISHED, SUPERSEDED }
}