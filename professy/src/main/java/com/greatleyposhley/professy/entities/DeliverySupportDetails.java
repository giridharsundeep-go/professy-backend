package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "delivery_support_details")
@Entity
public class DeliverySupportDetails {
    @Id
    @Column(name = "case_id", nullable = true, columnDefinition = "BIGINT")
    private Long caseId;
    @Column(name = "category", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'INCIDENT'")
    private String category;
    @Column(name = "resolution", nullable = true, columnDefinition = "TEXT")
    private String resolution;

    protected DeliverySupportDetails() {}
}
