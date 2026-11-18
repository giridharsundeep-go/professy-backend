package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "delivery_agent_config")
@Entity
public class DeliveryAgentConfig {
    @Id
    @Column(name = "agent_id", nullable = true, columnDefinition = "BIGINT")
    private Long agentId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean enabled;
    @Column(name = "skills_json", nullable = false, columnDefinition = "JSON")
    private String skillsJson;
    @Column(name = "worker_hash", nullable = true, columnDefinition = "CHAR(64)")
    private String workerHash;
    @Column(name = "last_seen", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime lastSeen;
    @Column(name = "model", nullable = false, columnDefinition = "VARCHAR(120)")
    private String model;
    @Column(name = "review_required", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean reviewRequired;
    @Column(name = "updated_by", nullable = false, columnDefinition = "BIGINT")
    private Long updatedBy;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    protected DeliveryAgentConfig() {}
}
