package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "document_revisions", uniqueConstraints = {@UniqueConstraint(name = "uk_document_revision", columnNames = {"page_id", "revision"})})
@Entity
public class DocumentRevisions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "page_id", nullable = false, columnDefinition = "BIGINT")
    private Long pageId;
    @Column(name = "revision", nullable = false, columnDefinition = "BIGINT")
    private Long revision;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;
    @Column(name = "content", nullable = true, columnDefinition = "LONGTEXT")
    private String content;
    @Column(name = "saved_by", nullable = false, columnDefinition = "BIGINT")
    private Long savedBy;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected DocumentRevisions() {}
}
