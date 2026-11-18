package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "ticket_execution_specs")
@IdClass(TicketExecutionSpecs.Key.class)
@Entity
public class TicketExecutionSpecs {
    @Id
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Id
    @Column(name = "ticket_kind", nullable = false, columnDefinition = "VARCHAR(15)")
    private String ticketKind;
    @Id
    @Column(name = "ticket_id", nullable = false, columnDefinition = "BIGINT")
    private Long ticketId;
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Column(name = "spec_json", nullable = false, columnDefinition = "JSON")
    private String specJson;
    @Column(name = "revision", nullable = false, columnDefinition = "BIGINT DEFAULT 1")
    private Long revision;
    @Column(name = "updated_by", nullable = false, columnDefinition = "BIGINT")
    private Long updatedBy;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    public static class Key implements Serializable {
        public Long orgId;
        public String ticketKind;
        public Long ticketId;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(orgId, that.orgId) && Objects.equals(ticketKind, that.ticketKind) && Objects.equals(ticketId, that.ticketId);
        }
        @Override public int hashCode() { return Objects.hash(orgId, ticketKind, ticketId); }
    }

    protected TicketExecutionSpecs() {}
}
