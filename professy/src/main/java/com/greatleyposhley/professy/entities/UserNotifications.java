package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "user_notifications", indexes = {@Index(name = "ix_user_unread", columnList = "user_id,read_at,id")}, uniqueConstraints = {@UniqueConstraint(name = "uk_notification_event_user", columnNames = {"event_id", "user_id"})})
@Entity
public class UserNotifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "event_id", nullable = false, columnDefinition = "CHAR(36)")
    private String eventId;
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private Long userId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Column(name = "ticket_type", nullable = false, columnDefinition = "VARCHAR(20)")
    private String ticketType;
    @Column(name = "ticket_id", nullable = false, columnDefinition = "BIGINT")
    private Long ticketId;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(300)")
    private String title;
    @Column(name = "body", nullable = false, columnDefinition = "VARCHAR(600)")
    private String body;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;
    @Column(name = "read_at", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime readAt;

    protected UserNotifications() {}
}
