package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "document_metadata")
@Entity
public class DocumentMetadata {
    @Id
    @Column(name = "page_id", nullable = true, columnDefinition = "BIGINT")
    private Long pageId;
    @Column(name = "revision", nullable = false, columnDefinition = "BIGINT DEFAULT 1")
    private Long revision;

    protected DocumentMetadata() {}
}
