package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "ticket_watchers")
@IdClass(TicketWatchers.Key.class)
@Entity
public class TicketWatchers {
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
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private Long userId;

    public static class Key implements Serializable {
        public Long orgId;
        public String ticketType;
        public Long ticketId;
        public Long userId;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(orgId, that.orgId) && Objects.equals(ticketType, that.ticketType) && Objects.equals(ticketId, that.ticketId) && Objects.equals(userId, that.userId);
        }
        @Override public int hashCode() { return Objects.hash(orgId, ticketType, ticketId, userId); }
    }

    protected TicketWatchers() {}
}
