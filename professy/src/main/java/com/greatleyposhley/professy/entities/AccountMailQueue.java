package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "account_mail_queue")
@Entity
public class AccountMailQueue {
    @Id
    @Column(name = "user_id", nullable = true, columnDefinition = "BIGINT")
    private Long userId;
    @Column(name = "reason", nullable = false, columnDefinition = "VARCHAR(30)")
    private String reason;
    @Column(name = "requested_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime requestedAt;
    @Column(name = "sent_at", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime sentAt;
    @Column(name = "attempts", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer attempts;
    @Column(name = "next_attempt_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime nextAttemptAt;

    protected AccountMailQueue() {}
}
