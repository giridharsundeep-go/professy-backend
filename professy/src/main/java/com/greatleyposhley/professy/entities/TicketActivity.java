package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "ticket_activity", indexes = {@Index(name = "ix_activity_ticket", columnList = "org_id,ticket_type,ticket_id,id")})
@Entity
public class TicketActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "event_id", nullable = false, columnDefinition = "CHAR(36)", unique = true)
    private String eventId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Column(name = "ticket_type", nullable = false, columnDefinition = "VARCHAR(20)")
    private String ticketType;
    @Column(name = "ticket_id", nullable = false, columnDefinition = "BIGINT")
    private Long ticketId;
    @Column(name = "actor_id", nullable = false, columnDefinition = "BIGINT")
    private Long actorId;
    @Column(name = "event_type", nullable = false, columnDefinition = "VARCHAR(50)")
    private String eventType;
    @Column(name = "summary", nullable = false, columnDefinition = "VARCHAR(500)")
    private String summary;
    @Column(name = "changes_json", nullable = false, columnDefinition = "JSON")
    private String changesJson;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected TicketActivity() {}
}
