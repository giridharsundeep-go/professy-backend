package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "delivery_support_activity", indexes = {@Index(name = "case_activity", columnList = "org_id,case_id,id")})
@Entity
public class DeliverySupportActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "case_id", nullable = false, columnDefinition = "BIGINT")
    private Long caseId;
    @Column(name = "actor_id", nullable = false, columnDefinition = "BIGINT")
    private Long actorId;
    @Column(name = "actor_name", nullable = false, columnDefinition = "VARCHAR(240)")
    private String actorName;
    @Column(name = "kind", nullable = false, columnDefinition = "VARCHAR(20)")
    private String kind;
    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected DeliverySupportActivity() {}
}
