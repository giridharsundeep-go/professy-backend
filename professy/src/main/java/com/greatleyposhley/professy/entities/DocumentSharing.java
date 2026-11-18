package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "document_sharing")
@Entity
public class DocumentSharing {
    @Id
    @Column(name = "page_id", nullable = true, columnDefinition = "BIGINT")
    private Long pageId;
    @Column(name = "general_access", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'RESTRICTED'")
    private String generalAccess;

    protected DocumentSharing() {}
}
