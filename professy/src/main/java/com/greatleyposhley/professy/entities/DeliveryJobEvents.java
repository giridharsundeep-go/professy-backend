package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "delivery_job_events", indexes = {@Index(name = "job_timeline", columnList = "org_id,job_id,id")}, uniqueConstraints = {@UniqueConstraint(name = "event_retry", columnNames = {"job_id", "worker_sequence"})})
@Entity
public class DeliveryJobEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "job_id", nullable = false, columnDefinition = "CHAR(32)")
    private String jobId;
    @Column(name = "worker_sequence", nullable = false, columnDefinition = "BIGINT")
    private Long workerSequence;
    @Column(name = "event", nullable = false, columnDefinition = "VARCHAR(80)")
    private String event;
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;
    @Column(name = "occurred_at", nullable = false, columnDefinition = "VARCHAR(60)")
    private String occurredAt;
    @Column(name = "received_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime receivedAt;

    protected DeliveryJobEvents() {}
}
