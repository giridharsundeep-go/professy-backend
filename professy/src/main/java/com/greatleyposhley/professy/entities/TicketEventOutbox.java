package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "ticket_event_outbox", indexes = {@Index(name = "ix_outbox_pending", columnList = "published_at,next_attempt_at,id")})
@Entity
public class TicketEventOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "event_id", nullable = false, columnDefinition = "CHAR(36)", unique = true)
    private String eventId;
    @Column(name = "aggregate_key", nullable = false, columnDefinition = "VARCHAR(100)")
    private String aggregateKey;
    @Column(name = "payload", nullable = false, columnDefinition = "JSON")
    private String payload;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;
    @Column(name = "published_at", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime publishedAt;
    @Column(name = "attempts", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer attempts;
    @Column(name = "next_attempt_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime nextAttemptAt;

    protected TicketEventOutbox() {}
}
