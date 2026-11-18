package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "sprint_retrospectives")
@Entity
public class SprintRetrospectives {
    @Id
    @Column(name = "sprint_id", nullable = true, columnDefinition = "BIGINT")
    private Long sprintId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Column(name = "state", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'COLLECTING'")
    private String state;
    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;
    @Column(name = "revision", nullable = false, columnDefinition = "BIGINT DEFAULT 1")
    private Long revision;
    @Column(name = "updated_by", nullable = false, columnDefinition = "BIGINT")
    private Long updatedBy;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    protected SprintRetrospectives() {}
}
