package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "user_permission_overrides")
@IdClass(UserPermissionOverrides.Key.class)
@Entity
public class UserPermissionOverrides {
    @Id
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private Long userId;
    @Id
    @Column(name = "permission", nullable = false, columnDefinition = "VARCHAR(80)")
    private String permission;
    @Column(name = "allowed", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean allowed;

    public static class Key implements Serializable {
        public Long orgId;
        public Long userId;
        public String permission;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(orgId, that.orgId) && Objects.equals(userId, that.userId) && Objects.equals(permission, that.permission);
        }
        @Override public int hashCode() { return Objects.hash(orgId, userId, permission); }
    }

    protected UserPermissionOverrides() {}
}
