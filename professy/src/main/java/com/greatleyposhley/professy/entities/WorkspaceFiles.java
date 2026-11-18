package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_files", indexes = {@Index(name = "ix_files_owner", columnList = "org_id,owner_id,file_type")})
@Entity
public class WorkspaceFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "file_type", nullable = false, columnDefinition = "VARCHAR(20)")
    private String fileType;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;
    @Column(name = "owner_id", nullable = false, columnDefinition = "BIGINT")
    private Long ownerId;
    @Column(name = "content_json", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String contentJson;
    @Column(name = "tags_json", nullable = false, columnDefinition = "TEXT")
    private String tagsJson;
    @Column(name = "revision", nullable = false, columnDefinition = "BIGINT DEFAULT 1")
    private Long revision;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    protected WorkspaceFiles() {}
}
