package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "delivery_support", indexes = {@Index(name = "support_scope", columnList = "org_id,project_id,id")})
@Entity
public class DeliverySupport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Column(name = "service_id", nullable = false, columnDefinition = "BIGINT")
    private Long serviceId;
    @Column(name = "issue_id", nullable = false, columnDefinition = "BIGINT")
    private Long issueId;
    @Column(name = "requester_id", nullable = false, columnDefinition = "BIGINT")
    private Long requesterId;
    @Column(name = "agent_id", nullable = true, columnDefinition = "BIGINT")
    private Long agentId;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "priority", nullable = false, columnDefinition = "VARCHAR(15)")
    private String priority;
    @Column(name = "state", nullable = false, columnDefinition = "VARCHAR(25) DEFAULT 'OPEN'")
    private String state;
    @Column(name = "workflow", nullable = false, columnDefinition = "VARCHAR(50)")
    private String workflow;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    protected DeliverySupport() {}
}
