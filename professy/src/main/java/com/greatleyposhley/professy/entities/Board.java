package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;

@Entity
@Table(
        name = "boards",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_board_project_name",
                        columnNames = {"project_id", "name"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_board_project_id",
                        columnList = "project_id"
                ),
                @Index(
                        name = "idx_board_owner_user_id",
                        columnList = "owner_user_id"
                ),
                @Index(
                        name = "idx_board_status",
                        columnList = "status"
                )
        }
)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_id", nullable = false)
    private Long orgId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "ACTIVE";

    public Board() {
    }

    public Board(
            Long projectId,
            Long ownerUserId,
            String name,
            String description,
            String status
    ) {
        this.projectId = projectId;
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

}