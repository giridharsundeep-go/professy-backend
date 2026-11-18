package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "agent_permission_overrides")
@IdClass(AgentPermissionOverrides.Key.class)
@Entity
public class AgentPermissionOverrides {
    @Id
    @Column(name = "actor_id", nullable = false, columnDefinition = "BIGINT")
    private Long actorId;
    @Id
    @Column(name = "permission", nullable = false, columnDefinition = "VARCHAR(80)")
    private String permission;
    @Column(name = "allowed", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean allowed;

    public static class Key implements Serializable {
        public Long actorId;
        public String permission;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(actorId, that.actorId) && Objects.equals(permission, that.permission);
        }
        @Override public int hashCode() { return Objects.hash(actorId, permission); }
    }

    protected AgentPermissionOverrides() {}
}
