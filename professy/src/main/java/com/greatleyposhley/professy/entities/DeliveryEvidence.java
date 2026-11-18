package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "delivery_evidence")
@Entity
public class DeliveryEvidence {
    @Id
    @Column(name = "attachment_id", nullable = true, columnDefinition = "BIGINT")
    private Long attachmentId;
    @Column(name = "job_id", nullable = false, columnDefinition = "CHAR(32)")
    private String jobId;
    @Column(name = "agent_id", nullable = false, columnDefinition = "BIGINT")
    private Long agentId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;

    protected DeliveryEvidence() {}
}
