package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "user_workspace_roles")
@IdClass(UserWorkspaceRoles.Key.class)
@Entity
public class UserWorkspaceRoles {
    @Id
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private Long userId;
    @Id
    @Column(name = "role_code", nullable = false, columnDefinition = "VARCHAR(50)")
    private String roleCode;

    public static class Key implements Serializable {
        public Long orgId;
        public Long userId;
        public String roleCode;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(orgId, that.orgId) && Objects.equals(userId, that.userId) && Objects.equals(roleCode, that.roleCode);
        }
        @Override public int hashCode() { return Objects.hash(orgId, userId, roleCode); }
    }

    protected UserWorkspaceRoles() {}
}
