package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_statuses")
@IdClass(WorkspaceStatuses.Key.class)
@Entity
public class WorkspaceStatuses {
    @Id
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Id
    @Column(name = "code", nullable = false, columnDefinition = "VARCHAR(50)")
    private String code;
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(100)")
    private String name;
    @Column(name = "color", nullable = false, columnDefinition = "VARCHAR(7)")
    private String color;
    @Column(name = "category", nullable = false, columnDefinition = "VARCHAR(20)")
    private String category;
    @Column(name = "sort_order", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder;
    @Column(name = "active", nullable = false, columnDefinition = "BOOLEAN DEFAULT 1")
    private Boolean active;

    public static class Key implements Serializable {
        public Long orgId;
        public String code;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(orgId, that.orgId) && Objects.equals(code, that.code);
        }
        @Override public int hashCode() { return Objects.hash(orgId, code); }
    }

    protected WorkspaceStatuses() {}
}
