package com.greatleyposhley.professy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "doc_folders",
        indexes = {
                @Index(name = "idx_doc_folder_space", columnList = "space_id"),
                @Index(name = "idx_doc_folder_parent", columnList = "parent_folder_id")
        }
)
public class DocFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "space_id", nullable = false)
    @JsonIgnore
    private DocSpace docSpace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    @JsonIgnore
    private DocFolder parentFolder;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    @JsonProperty("spaceId")
    public Long getSpaceId() {
        return docSpace != null ? docSpace.getId() : null;
    }

    @Transient
    @JsonProperty("parentFolderId")
    public Long getParentFolderId() {
        return parentFolder != null ? parentFolder.getId() : null;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocSpace getDocSpace() {
        return docSpace;
    }

    public void setDocSpace(DocSpace docSpace) {
        this.docSpace = docSpace;
    }

    public DocFolder getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(DocFolder parentFolder) {
        this.parentFolder = parentFolder;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "DocFolder{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", docSpace=" + docSpace +
                ", parentFolder=" + parentFolder +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
