package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "organization_units", uniqueConstraints = {@UniqueConstraint(name = "uk_org_unit", columnNames = {"org_id", "unit_type", "name"})})
@Entity
public class OrganizationUnits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "org_id", nullable = false, columnDefinition = "BIGINT")
    private Long orgId;
    @Column(name = "unit_type", nullable = false, columnDefinition = "VARCHAR(20)")
    private String unitType;
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(120)")
    private String name;
    @Column(name = "parent_id", nullable = true, columnDefinition = "BIGINT")
    private Long parentId;
    @Column(name = "active", nullable = false, columnDefinition = "BOOLEAN DEFAULT 1")
    private Boolean active;

    protected OrganizationUnits() {}
}
