package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "delivery_architectures")
@IdClass(DeliveryArchitectures.Key.class)
@Entity
public class DeliveryArchitectures {
    @Id
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Id
    @Column(name = "project_id", nullable = false, columnDefinition = "BIGINT")
    private Long projectId;
    @Column(name = "revision", nullable = false, columnDefinition = "BIGINT DEFAULT 1")
    private Long revision;
    @Column(name = "graph_json", nullable = false, columnDefinition = "JSON")
    private String graphJson;
    @Column(name = "updated_by", nullable = false, columnDefinition = "BIGINT")
    private Long updatedBy;
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime updatedAt;

    public static class Key implements Serializable {
        public Long orgId;
        public Long projectId;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(orgId, that.orgId) && Objects.equals(projectId, that.projectId);
        }
        @Override public int hashCode() { return Objects.hash(orgId, projectId); }
    }

    protected DeliveryArchitectures() {}
}
