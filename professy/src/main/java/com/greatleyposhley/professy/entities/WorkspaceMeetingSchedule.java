package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_meeting_schedule")
@Entity
public class WorkspaceMeetingSchedule {
    @Id
    @Column(name = "meeting_id", nullable = true, columnDefinition = "BIGINT")
    private Long meetingId;
    @Column(name = "time_zone", nullable = false, columnDefinition = "VARCHAR(80)")
    private String timeZone;
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "revision", nullable = false, columnDefinition = "BIGINT DEFAULT 1")
    private Long revision;

    protected WorkspaceMeetingSchedule() {}
}
