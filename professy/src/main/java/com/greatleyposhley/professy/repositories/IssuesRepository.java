package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Issues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssuesRepository extends JpaRepository<Issues, Long> {

    Optional<Issues> findByOrgIdAndIssueCode(Long orgId, String issueCode);

    // Attribute Filters
    List<Issues> findByOrgIdAndProjectId(Long orgId, Long projectId);

    List<Issues> findByOrgIdAndSprintId(Long orgId, Long sprintId);

    List<Issues> findByOrgIdAndAssigneeId(Long orgId, Long assigneeId);

    List<Issues> findByOrgIdAndReporterId(Long orgId, Long reporterId);

    List<Issues> findByOrgIdAndStatus(Long orgId, String status);

    List<Issues> findByOrgIdAndProjectIdAndSprintId(Long orgId, Long projectId, Long sprintId);

    // Find Issues by Epic ID
    @Query("SELECT DISTINCT i FROM Issues i JOIN i.allocations a " +
            "WHERE i.orgId = :orgId " +
            "AND a.allocatableType = com.greatleyposhley.professy.entities.AllocatableType.EPIC " +
            "AND a.allocatableId = :epicId")
    List<Issues> findByEpicId(@Param("orgId") Long orgId, @Param("epicId") Long epicId);

    // Find Issues by Story ID
    @Query("SELECT DISTINCT i FROM Issues i JOIN i.allocations a " +
            "WHERE i.orgId = :orgId " +
            "AND a.allocatableType = com.greatleyposhley.professy.entities.AllocatableType.STORY " +
            "AND a.allocatableId = :storyId")
    List<Issues> findByStoryId(@Param("orgId") Long orgId, @Param("storyId") Long storyId);

    // Find Issues by Task ID
    @Query("SELECT DISTINCT i FROM Issues i JOIN i.allocations a " +
            "WHERE i.orgId = :orgId " +
            "AND a.allocatableType = com.greatleyposhley.professy.entities.AllocatableType.TASK " +
            "AND a.allocatableId = :taskId")
    List<Issues> findByTaskId(@Param("orgId") Long orgId, @Param("taskId") Long taskId);

    @Query("SELECT i FROM Issues i LEFT JOIN FETCH i.allocations " +
            "WHERE i.orgId = :orgId AND i.id = :id")
    Optional<Issues> findByIdWithAllocations(
            @Param("orgId") Long orgId,
            @Param("id") Long id
    );

    @Query("SELECT DISTINCT i FROM Issues i LEFT JOIN FETCH i.allocations " +
            "WHERE i.orgId = :orgId")
    List<Issues> findAllWithAllocations(@Param("orgId") Long orgId);
}
