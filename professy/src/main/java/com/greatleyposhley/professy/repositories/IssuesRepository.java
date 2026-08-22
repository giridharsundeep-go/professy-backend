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

    Optional<Issues> findByIssueCode(String issueCode);

    // 🔍 Attribute Filters
    List<Issues> findByProjectId(Long projectId);

    List<Issues> findBySprintId(Long sprintId);

    List<Issues> findByAssigneeId(Long assigneeId);

    List<Issues> findByReporterId(Long reporterId);

    List<Issues> findByStatus(String status);

    List<Issues> findByProjectIdAndSprintId(Long projectId, Long sprintId);

    // 🔍 Find Issues by Epic ID
    @Query("SELECT DISTINCT i FROM Issues i JOIN i.allocations a " +
            "WHERE a.allocatableType = com.greatleyposhley.professy.entities.AllocatableType.EPIC " +
            "AND a.allocatableId = :epicId")
    List<Issues> findByEpicId(@Param("epicId") Long epicId);

    // 🔍 Find Issues by Story ID
    @Query("SELECT DISTINCT i FROM Issues i JOIN i.allocations a " +
            "WHERE a.allocatableType = com.greatleyposhley.professy.entities.AllocatableType.STORY " +
            "AND a.allocatableId = :storyId")
    List<Issues> findByStoryId(@Param("storyId") Long storyId);

    // 🔍 Find Issues by Task ID
    @Query("SELECT DISTINCT i FROM Issues i JOIN i.allocations a " +
            "WHERE a.allocatableType = com.greatleyposhley.professy.entities.AllocatableType.TASK " +
            "AND a.allocatableId = :taskId")
    List<Issues> findByTaskId(@Param("taskId") Long taskId);

    @Query("SELECT i FROM Issues i LEFT JOIN FETCH i.allocations WHERE i.id = :id")
    Optional<Issues> findByIdWithAllocations(@Param("id") Long id);

    @Query("SELECT DISTINCT i FROM Issues i LEFT JOIN FETCH i.allocations")
    List<Issues> findAllWithAllocations();
}