package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "delivery_worker_reports")
@Entity
public class DeliveryWorkerReports {
    @Id
    @Column(name = "agent_id", nullable = true, columnDefinition = "BIGINT")
    private Long agentId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "worker_id", nullable = false, columnDefinition = "VARCHAR(100)")
    private String workerId;
    @Column(name = "report_json", nullable = false, columnDefinition = "JSON")
    private String reportJson;
    @Column(name = "reported_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime reportedAt;

    protected DeliveryWorkerReports() {}
}
