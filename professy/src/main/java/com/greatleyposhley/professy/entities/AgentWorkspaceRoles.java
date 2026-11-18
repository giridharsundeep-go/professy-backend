package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "agent_workspace_roles")
@IdClass(AgentWorkspaceRoles.Key.class)
@Entity
public class AgentWorkspaceRoles {
    @Id
    @Column(name = "actor_id", nullable = false, columnDefinition = "BIGINT")
    private Long actorId;
    @Id
    @Column(name = "role_code", nullable = false, columnDefinition = "VARCHAR(50)")
    private String roleCode;

    public static class Key implements Serializable {
        public Long actorId;
        public String roleCode;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(actorId, that.actorId) && Objects.equals(roleCode, that.roleCode);
        }
        @Override public int hashCode() { return Objects.hash(actorId, roleCode); }
    }

    protected AgentWorkspaceRoles() {}
}
