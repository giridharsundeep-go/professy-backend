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

    // Python: get_all_sprints()
    List<Sprints> findAllByOrderByIdAsc();

    // Python: get_sprints_by_project_id()
    List<Sprints> findAllByProject_IdOrderBySprintNumberAsc(
            Long projectId
    );

    // Used by create/sync because DB has:
    // UNIQUE(project_id, sprint_number)
    Optional<Sprints> findByProject_IdAndSprintNumber(
            Long projectId,
            Integer sprintNumber
    );

    // Python: get_automatic_sprints_to_activate()
    List<Sprints>
    findAllByStatusAndActivationTypeAndScheduledStartDateLessThanEqual(
            String status,
            String activationType,
            LocalDate checkDate
    );

    // Used by sync_and_recreate_planned_sprints()
    List<Sprints> findAllByProject_IdAndStatus(
            Long projectId,
            String status
    );

    @Query("""
        SELECT s
        FROM Sprints s
        WHERE s.project.id = :projectId
        ORDER BY s.sprintNumber ASC
    """)
    List<Sprints> findSprintRowsByProjectId(@Param("projectId") Long projectId);


}