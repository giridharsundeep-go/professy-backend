package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "ticket_documents")
@IdClass(TicketDocuments.Key.class)
@Entity
public class TicketDocuments {
    @Id
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Id
    @Column(name = "ticket_type", nullable = false, columnDefinition = "VARCHAR(20)")
    private String ticketType;
    @Id
    @Column(name = "ticket_id", nullable = false, columnDefinition = "BIGINT")
    private Long ticketId;
    @Id
    @Column(name = "document_id", nullable = false, columnDefinition = "BIGINT")
    private Long documentId;
    @Column(name = "linked_by", nullable = false, columnDefinition = "BIGINT")
    private Long linkedBy;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    public static class Key implements Serializable {
        public Long orgId;
        public String ticketType;
        public Long ticketId;
        public Long documentId;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(orgId, that.orgId) && Objects.equals(ticketType, that.ticketType) && Objects.equals(ticketId, that.ticketId) && Objects.equals(documentId, that.documentId);
        }
        @Override public int hashCode() { return Objects.hash(orgId, ticketType, ticketId, documentId); }
    }

    protected TicketDocuments() {}
}
