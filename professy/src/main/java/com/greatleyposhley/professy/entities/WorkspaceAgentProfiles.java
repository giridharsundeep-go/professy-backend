package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_agent_profiles")
@Entity
public class WorkspaceAgentProfiles {
    @Id
    @Column(name = "actor_id", nullable = true, columnDefinition = "BIGINT")
    private Long actorId;
    @Column(name = "designation", nullable = false, columnDefinition = "VARCHAR(120) DEFAULT ''")
    private String designation;
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "instructions", nullable = false, columnDefinition = "TEXT")
    private String instructions;
    @Column(name = "model", nullable = false, columnDefinition = "VARCHAR(120) DEFAULT ''")
    private String model;

    protected WorkspaceAgentProfiles() {}
}
