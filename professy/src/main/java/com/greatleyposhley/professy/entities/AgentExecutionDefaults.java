package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "agent_execution_defaults")
@Entity
public class AgentExecutionDefaults {
    @Id
    @Column(name = "agent_id", nullable = true, columnDefinition = "BIGINT")
    private Long agentId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "defaults_json", nullable = false, columnDefinition = "JSON")
    private String defaultsJson;
    @Column(name = "updated_by", nullable = false, columnDefinition = "BIGINT")
    private Long updatedBy;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    protected AgentExecutionDefaults() {}
}
