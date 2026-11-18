package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_meeting_agent_sessions", indexes = {@Index(name = "ix_meeting_agent_session", columnList = "meeting_id,agent_id")})
@Entity
public class WorkspaceMeetingAgentSessions {
    @Id
    @Column(name = "token_hash", nullable = true, columnDefinition = "CHAR(64)")
    private String tokenHash;
    @Column(name = "meeting_id", nullable = false, columnDefinition = "BIGINT")
    private Long meetingId;
    @Column(name = "agent_id", nullable = false, columnDefinition = "BIGINT")
    private Long agentId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "created_by", nullable = false, columnDefinition = "BIGINT")
    private Long createdBy;
    @Column(name = "expires_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime expiresAt;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected WorkspaceMeetingAgentSessions() {}
}
