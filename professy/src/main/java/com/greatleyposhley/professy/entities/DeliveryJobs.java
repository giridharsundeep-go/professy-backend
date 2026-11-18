package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "delivery_jobs", indexes = {@Index(name = "jobs_queue", columnList = "agent_id,state,created_at"), @Index(name = "jobs_ticket", columnList = "org_id,ticket_kind,ticket_id")})
@Entity
public class DeliveryJobs {
    @Id
    @Column(name = "id", nullable = true, columnDefinition = "CHAR(32)")
    private String id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Column(name = "support_id", nullable = true, columnDefinition = "BIGINT")
    private Long supportId;
    @Column(name = "ticket_kind", nullable = false, columnDefinition = "VARCHAR(15)")
    private String ticketKind;
    @Column(name = "ticket_id", nullable = false, columnDefinition = "BIGINT")
    private Long ticketId;
    @Column(name = "service_id", nullable = false, columnDefinition = "BIGINT")
    private Long serviceId;
    @Column(name = "agent_id", nullable = false, columnDefinition = "BIGINT")
    private Long agentId;
    @Column(name = "requested_by", nullable = false, columnDefinition = "BIGINT")
    private Long requestedBy;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;
    @Column(name = "instructions", nullable = false, columnDefinition = "TEXT")
    private String instructions;
    @Column(name = "workflow", nullable = false, columnDefinition = "VARCHAR(50)")
    private String workflow;
    @Column(name = "state", nullable = false, columnDefinition = "VARCHAR(25) DEFAULT 'QUEUED'")
    private String state;
    @Column(name = "lease_until", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime leaseUntil;
    @Column(name = "lease_token", nullable = true, columnDefinition = "CHAR(64)")
    private String leaseToken;
    @Column(name = "worker_id", nullable = true, columnDefinition = "VARCHAR(100)")
    private String workerId;
    @Column(name = "result_json", nullable = true, columnDefinition = "JSON")
    private String resultJson;
    @Column(name = "attachment_id", nullable = true, columnDefinition = "BIGINT")
    private Long attachmentId;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    protected DeliveryJobs() {}
}
