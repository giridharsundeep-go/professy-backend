package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "incident_ticket_links")
@IdClass(IncidentTicketLinks.Key.class)
@Entity
public class IncidentTicketLinks {
    @Id
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Id
    @Column(name = "incident_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String incidentId;
    @Column(name = "issue_id", nullable = true, columnDefinition = "BIGINT")
    private Long issueId;

    public static class Key implements Serializable {
        public Long orgId;
        public String incidentId;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(orgId, that.orgId) && Objects.equals(incidentId, that.incidentId);
        }
        @Override public int hashCode() { return Objects.hash(orgId, incidentId); }
    }

    protected IncidentTicketLinks() {}
}
