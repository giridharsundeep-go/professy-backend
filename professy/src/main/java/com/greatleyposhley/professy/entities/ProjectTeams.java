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
@Table(name = "project_teams")
public class ProjectTeams {

    @EmbeddedId
    private ProjectTeamId id = new ProjectTeamId();

    // Foreign Key to projects table
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    private Projects project;

    // Foreign Key to teams table
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId")
    @JoinColumn(name = "team_id", nullable = false)
    private Teams team;

    // Foreign Key to user_account table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    // --- Embedded Composite Key ---

    @Embeddable
    public static class ProjectTeamId implements Serializable {

        @Column(name = "project_id")
        private Long projectId;

        @Column(name = "team_id")
        private Long teamId;

        public ProjectTeamId() {
        }

        public ProjectTeamId(Long projectId, Long teamId) {
            this.projectId = projectId;
            this.teamId = teamId;
        }

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }

        public Long getTeamId() {
            return teamId;
        }

        public void setTeamId(Long teamId) {
            this.teamId = teamId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProjectTeamId that = (ProjectTeamId) o;
            return Objects.equals(projectId, that.projectId) &&
                    Objects.equals(teamId, that.teamId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(projectId, teamId);
        }
    }

    // --- Constructors ---

    public ProjectTeams() {
    }

    public ProjectTeams(Projects project, Teams team, UserAccount userAccount) {
        this.project = project;
        this.team = team;
        this.userAccount = userAccount;
        this.id = new ProjectTeamId(project.getId(), team.getId());
    }

    // --- Getters and Setters ---

    public ProjectTeamId getId() {
        return id;
    }

    public void setId(ProjectTeamId id) {
        this.id = id;
    }

    public Projects getProject() {
        return project;
    }

    public void setProject(Projects project) {
        this.project = project;
    }

    public Teams getTeam() {
        return team;
    }

    public void setTeam(Teams team) {
        this.team = team;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}