package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "delivery_release_services", uniqueConstraints = {@UniqueConstraint(name = "job_release", columnNames = {"job_id"})})
@IdClass(DeliveryReleaseServices.Key.class)
@Entity
public class DeliveryReleaseServices {
    @Id
    @Column(name = "release_id", nullable = false, columnDefinition = "BIGINT")
    private Long releaseId;
    @Id
    @Column(name = "service_id", nullable = false, columnDefinition = "BIGINT")
    private Long serviceId;
    @Column(name = "job_id", nullable = true, columnDefinition = "CHAR(32)")
    private String jobId;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;

    public static class Key implements Serializable {
        public Long releaseId;
        public Long serviceId;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(releaseId, that.releaseId) && Objects.equals(serviceId, that.serviceId);
        }
        @Override public int hashCode() { return Objects.hash(releaseId, serviceId); }
    }

    protected DeliveryReleaseServices() {}
}
