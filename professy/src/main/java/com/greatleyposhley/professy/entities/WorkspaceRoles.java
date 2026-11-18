package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_roles")
@IdClass(WorkspaceRoles.Key.class)
@Entity
public class WorkspaceRoles {
    @Id
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Id
    @Column(name = "code", nullable = false, columnDefinition = "VARCHAR(50)")
    private String code;
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(100)")
    private String name;
    @Column(name = "permissions_json", nullable = false, columnDefinition = "JSON")
    private String permissionsJson;
    @Column(name = "system_role", nullable = false, columnDefinition = "BOOLEAN DEFAULT 0")
    private Boolean systemRole;

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

    protected WorkspaceRoles() {}
}
