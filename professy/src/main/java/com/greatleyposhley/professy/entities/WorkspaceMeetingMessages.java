package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_meeting_messages")
@Entity
public class WorkspaceMeetingMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "meeting_id", nullable = false, columnDefinition = "BIGINT")
    private Long meetingId;
    @Column(name = "author_id", nullable = false, columnDefinition = "BIGINT")
    private Long authorId;
    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected WorkspaceMeetingMessages() {}
}
