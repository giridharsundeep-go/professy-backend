package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Sprints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SprintsRepository extends JpaRepository<Sprints, Long> {

    // Get all sprints for an organization
    List<Sprints> findAllByOrgIdOrderByIdAsc(Long orgId);

    // Get sprints by organization and project
    List<Sprints> findAllByOrgIdAndProject_IdOrderBySprintNumberAsc(
            Long orgId,
            Long projectId
    );

    // Used by create/sync because DB has:
    // UNIQUE(project_id, sprint_number)
    Optional<Sprints> findByOrgIdAndProject_IdAndSprintNumber(
            Long orgId,
            Long projectId,
            Integer sprintNumber
    );

    // Get automatic sprints to activate for an organization
    List<Sprints>
    findAllByOrgIdAndStatusAndActivationTypeAndScheduledStartDateLessThanEqual(
            Long orgId,
            String status,
            String activationType,
            LocalDate checkDate
    );

    // Used by sync_and_recreate_planned_sprints()
    List<Sprints> findAllByOrgIdAndProject_IdAndStatus(
            Long orgId,
            Long projectId,
            String status
    );

    @Query("""
        SELECT s
        FROM Sprints s
        WHERE s.orgId = :orgId
          AND s.project.id = :projectId
        ORDER BY s.sprintNumber ASC
    """)
    List<Sprints> findSprintRowsByProjectId(
            @Param("orgId") Long orgId,
            @Param("projectId") Long projectId
    );
}
