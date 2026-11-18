package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "document_comments", indexes = {@Index(name = "ix_document_comments", columnList = "page_id,id")})
@Entity
public class DocumentComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "page_id", nullable = false, columnDefinition = "BIGINT")
    private Long pageId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "author_id", nullable = false, columnDefinition = "BIGINT")
    private Long authorId;
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "resolved", nullable = false, columnDefinition = "BOOLEAN DEFAULT 0")
    private Boolean resolved;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected DocumentComments() {}
}
