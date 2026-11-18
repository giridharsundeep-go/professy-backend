package com.greatleyposhley.professy.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Retains the historical schema ledger table after SQL migrations were removed. */
@Entity
@Table(name = "workspace_schema_migrations")
public class WorkspaceSchemaMigrations {
    @Id
    @Column(name = "version", length = 100, nullable = false)
    private String version;

    @Column(name = "applied_at", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private LocalDateTime appliedAt;

    protected WorkspaceSchemaMigrations() { }
}
