package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "email_verification_tokens", indexes = {@Index(name = "ix_email_verification_user", columnList = "user_id,expires_at")})
@Entity
public class EmailVerificationTokens {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private Long userId;
    @Column(name = "token_hash", nullable = false, columnDefinition = "CHAR(64)", unique = true)
    private String tokenHash;
    @Column(name = "expires_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime expiresAt;
    @Column(name = "used_at", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime usedAt;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected EmailVerificationTokens() {}
}
