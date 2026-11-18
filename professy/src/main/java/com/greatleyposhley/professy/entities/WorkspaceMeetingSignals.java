package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_meeting_signals", indexes = {@Index(name = "ix_signal_delivery", columnList = "meeting_id,recipient_id,id")})
@Entity
public class WorkspaceMeetingSignals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "meeting_id", nullable = false, columnDefinition = "BIGINT")
    private Long meetingId;
    @Column(name = "sender_id", nullable = false, columnDefinition = "BIGINT")
    private Long senderId;
    @Column(name = "recipient_id", nullable = false, columnDefinition = "BIGINT")
    private Long recipientId;
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected WorkspaceMeetingSignals() {}
}
