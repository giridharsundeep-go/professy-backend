package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "auth_request_limits")
@Entity
public class AuthRequestLimits {
    @Id
    @Column(name = "request_key", nullable = true, columnDefinition = "CHAR(64)")
    private String requestKey;
    @Column(name = "attempts", nullable = false, columnDefinition = "INT")
    private Integer attempts;
    @Column(name = "expires_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime expiresAt;

    protected AuthRequestLimits() {}
}
