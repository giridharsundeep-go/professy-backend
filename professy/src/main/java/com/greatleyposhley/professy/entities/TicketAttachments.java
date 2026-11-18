package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "ticket_attachments", indexes = {@Index(name = "ix_attachments_ticket", columnList = "org_id,ticket_type,ticket_id,id")})
@Entity
public class TicketAttachments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Column(name = "ticket_type", nullable = false, columnDefinition = "VARCHAR(20)")
    private String ticketType;
    @Column(name = "ticket_id", nullable = false, columnDefinition = "BIGINT")
    private Long ticketId;
    @Column(name = "uploaded_by", nullable = false, columnDefinition = "BIGINT")
    private Long uploadedBy;
    @Column(name = "original_name", nullable = false, columnDefinition = "VARCHAR(200)")
    private String originalName;
    @Column(name = "storage_name", nullable = false, columnDefinition = "VARCHAR(255)", unique = true)
    private String storageName;
    @Column(name = "content_type", nullable = false, columnDefinition = "VARCHAR(150)")
    private String contentType;
    @Column(name = "byte_size", nullable = false, columnDefinition = "BIGINT")
    private Long byteSize;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;
    @Column(name = "deleted_at", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime deletedAt;

    protected TicketAttachments() {}
}
