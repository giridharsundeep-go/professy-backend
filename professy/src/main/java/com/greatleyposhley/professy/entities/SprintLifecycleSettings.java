package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "sprint_lifecycle_settings")
@Entity
public class SprintLifecycleSettings {
    @Id
    @Column(name = "sprint_id", nullable = true, columnDefinition = "BIGINT")
    private Long sprintId;
    @Column(name = "time_zone", nullable = false, columnDefinition = "VARCHAR(80) DEFAULT 'Asia/Kolkata'")
    private String timeZone;
    @Column(name = "updated_by", nullable = true, columnDefinition = "BIGINT")
    private Long updatedBy;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    protected SprintLifecycleSettings() {}
}
