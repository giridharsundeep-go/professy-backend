package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "ticket_actor_assignments", indexes = {@Index(name = "ix_actor_work", columnList = "actor_id,project_id")})
@IdClass(TicketActorAssignments.Key.class)
@Entity
public class TicketActorAssignments {
    @Id
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Id
    @Column(name = "ticket_type", nullable = false, columnDefinition = "VARCHAR(20)")
    private String ticketType;
    @Id
    @Column(name = "ticket_id", nullable = false, columnDefinition = "BIGINT")
    private Long ticketId;
    @Id
    @Column(name = "assignment_role", nullable = false, columnDefinition = "VARCHAR(20)")
    private String assignmentRole;
    @Column(name = "actor_id", nullable = false, columnDefinition = "BIGINT")
    private Long actorId;
    @Column(name = "updated_by", nullable = false, columnDefinition = "BIGINT")
    private Long updatedBy;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    public static class Key implements Serializable {
        public Long orgId;
        public String ticketType;
        public Long ticketId;
        public String assignmentRole;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(orgId, that.orgId) && Objects.equals(ticketType, that.ticketType) && Objects.equals(ticketId, that.ticketId) && Objects.equals(assignmentRole, that.assignmentRole);
        }
        @Override public int hashCode() { return Objects.hash(orgId, ticketType, ticketId, assignmentRole); }
    }

    protected TicketActorAssignments() {}
}
