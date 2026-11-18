package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "account_handoff_codes", indexes = {@Index(name = "idx_handoff_expiry", columnList = "expires_at")})
@Entity
public class AccountHandoffCodes {
    @Id
    @Column(name = "code_hash", nullable = true, columnDefinition = "CHAR(64)")
    private String codeHash;
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private Long userId;
    @Column(name = "credential_version", nullable = false, columnDefinition = "INT")
    private Integer credentialVersion;
    @Column(name = "expires_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime expiresAt;
    @Column(name = "used_at", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime usedAt;

    protected AccountHandoffCodes() {}
}
