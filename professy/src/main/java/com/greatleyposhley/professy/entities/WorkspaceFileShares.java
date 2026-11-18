package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_file_shares")
@IdClass(WorkspaceFileShares.Key.class)
@Entity
public class WorkspaceFileShares {
    @Id
    @Column(name = "file_id", nullable = false, columnDefinition = "BIGINT")
    private Long fileId;
    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private Long userId;
    @Column(name = "permission", nullable = false, columnDefinition = "VARCHAR(10)")
    private String permission;

    public static class Key implements Serializable {
        public Long fileId;
        public Long userId;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(fileId, that.fileId) && Objects.equals(userId, that.userId);
        }
        @Override public int hashCode() { return Objects.hash(fileId, userId); }
    }

    protected WorkspaceFileShares() {}
}
