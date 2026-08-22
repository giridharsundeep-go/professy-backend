package com.greatleyposhley.professy.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "project_individual_members")
public class ProjectIndividualMembers {

    @EmbeddedId
    private ProjectIndividualMemberId id = new ProjectIndividualMemberId();

    // Foreign Key to projects table
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    private Projects project;

    // Foreign Key to user_account table (the assigned member)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userAccountId")
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    // Foreign Key to user_account table (the creator/assigner)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount creator;

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    // --- Embedded Composite Key ---

    @Embeddable
    public static class ProjectIndividualMemberId implements Serializable {

        @Column(name = "project_id")
        private Long projectId;

        @Column(name = "user_account_id")
        private Long userAccountId;

        public ProjectIndividualMemberId() {
        }

        public ProjectIndividualMemberId(Long projectId, Long userAccountId) {
            this.projectId = projectId;
            this.userAccountId = userAccountId;
        }

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }

        public Long getUserAccountId() {
            return userAccountId;
        }

        public void setUserAccountId(Long userAccountId) {
            this.userAccountId = userAccountId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProjectIndividualMemberId that = (ProjectIndividualMemberId) o;
            return Objects.equals(projectId, that.projectId) &&
                    Objects.equals(userAccountId, that.userAccountId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(projectId, userAccountId);
        }
    }

    // --- Constructors ---

    public ProjectIndividualMembers() {
    }

    public ProjectIndividualMembers(Projects project, UserAccount userAccount, UserAccount creator) {
        this.project = project;
        this.userAccount = userAccount;
        this.creator = creator;
        this.id = new ProjectIndividualMemberId(project.getId(), userAccount.getId());
    }

    // --- Getters and Setters ---

    public ProjectIndividualMemberId getId() {
        return id;
    }

    public void setId(ProjectIndividualMemberId id) {
        this.id = id;
    }

    public Projects getProject() {
        return project;
    }

    public void setProject(Projects project) {
        this.project = project;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public UserAccount getCreator() {
        return creator;
    }

    public void setCreator(UserAccount creator) {
        this.creator = creator;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}