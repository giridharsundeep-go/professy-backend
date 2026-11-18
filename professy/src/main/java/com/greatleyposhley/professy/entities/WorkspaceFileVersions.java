package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_file_versions")
@IdClass(WorkspaceFileVersions.Key.class)
@Entity
public class WorkspaceFileVersions {
    @Id
    @Column(name = "file_id", nullable = false, columnDefinition = "BIGINT")
    private Long fileId;
    @Id
    @Column(name = "revision", nullable = false, columnDefinition = "BIGINT")
    private Long revision;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;
    @Column(name = "content_json", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String contentJson;
    @Column(name = "saved_by", nullable = false, columnDefinition = "BIGINT")
    private Long savedBy;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    public static class Key implements Serializable {
        public Long fileId;
        public Long revision;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(fileId, that.fileId) && Objects.equals(revision, that.revision);
        }
        @Override public int hashCode() { return Objects.hash(fileId, revision); }
    }

    protected WorkspaceFileVersions() {}
}
