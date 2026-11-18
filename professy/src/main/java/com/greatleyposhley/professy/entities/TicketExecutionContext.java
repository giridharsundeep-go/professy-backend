package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "ticket_execution_context")
@Entity
public class TicketExecutionContext {
    @Id
    @Column(name = "job_id", nullable = true, columnDefinition = "CHAR(32)")
    private String jobId;
    @Column(name = "context_json", nullable = false, columnDefinition = "JSON")
    private String contextJson;

    protected TicketExecutionContext() {}
}
