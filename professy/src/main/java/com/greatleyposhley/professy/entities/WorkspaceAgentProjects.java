package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_agent_projects")
@IdClass(WorkspaceAgentProjects.Key.class)
@Entity
public class WorkspaceAgentProjects {
    @Id
    @Column(name = "actor_id", nullable = false, columnDefinition = "BIGINT")
    private Long actorId;
    @Id
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;

    public static class Key implements Serializable {
        public Long actorId;
        public Long projectId;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(actorId, that.actorId) && Objects.equals(projectId, that.projectId);
        }
        @Override public int hashCode() { return Objects.hash(actorId, projectId); }
    }

    protected WorkspaceAgentProjects() {}
}
