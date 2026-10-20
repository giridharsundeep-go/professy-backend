package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Projects;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProjectsRepository extends CrudRepository<Projects, Long> {

    @Query(value = "SELECT * FROM projects WHERE org_id = :orgId AND user_id = :userId ORDER BY created_at DESC", nativeQuery = true)
    List<Map<String, Object>> findProjectRowsByUserId(
            @Param("orgId") Long orgId,
            @Param("userId") Long userId
    );

    @Query(value = "SELECT * FROM projects WHERE org_id = :orgId AND product_id = :productId ORDER BY created_at DESC", nativeQuery = true)
    List<Map<String, Object>> findProjectRowsByProductId(
            @Param("orgId") Long orgId,
            @Param("productId") Long productId
    );

    @Query(value = "SELECT * FROM projects WHERE org_id = :orgId AND id = :projectId", nativeQuery = true)
    Map<String, Object> findProjectRowById(
            @Param("orgId") Long orgId,
            @Param("projectId") Long projectId
    );

    @Modifying
    @Query(value = """
            INSERT INTO projects (
                org_id,
                user_id,
                product_id,
                project_code,
                name,
                description,
                methodology,
                priority,
                total_backlog_points,
                sprint_duration_weeks,
                target_velocity,
                auto_rollover_backlog,
                computed_sprint_count,
                computed_total_duration_weeks
            )
            VALUES (
                :orgId,
                :userId,
                :productId,
                :projectCode,
                :name,
                :description,
                :methodology,
                :priority,
                :totalBacklogPoints,
                :sprintDurationWeeks,
                :targetVelocity,
                :autoRolloverBacklog,
                :computedSprintCount,
                :computedTotalDurationWeeks
            )
            """, nativeQuery = true)
    int insertProject(
            @Param("orgId") Long orgId,
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("projectCode") String projectCode,
            @Param("name") String name,
            @Param("description") String description,
            @Param("methodology") String methodology,
            @Param("priority") String priority,
            @Param("totalBacklogPoints") Integer totalBacklogPoints,
            @Param("sprintDurationWeeks") Integer sprintDurationWeeks,
            @Param("targetVelocity") Integer targetVelocity,
            @Param("autoRolloverBacklog") Boolean autoRolloverBacklog,
            @Param("computedSprintCount") Integer computedSprintCount,
            @Param("computedTotalDurationWeeks") Integer computedTotalDurationWeeks
    );

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Long getLastInsertedProjectId();

    @Modifying
    @Query(value = """
            UPDATE projects
            SET product_id = :productId,
                project_code = :projectCode,
                name = :name,
                description = :description,
                status = :status,
                methodology = :methodology,
                priority = :priority,
                total_backlog_points = :totalBacklogPoints,
                sprint_duration_weeks = :sprintDurationWeeks,
                target_velocity = :targetVelocity,
                auto_rollover_backlog = :autoRolloverBacklog,
                computed_sprint_count = :computedSprintCount,
                computed_total_duration_weeks = :computedTotalDurationWeeks
            WHERE id = :projectId
              AND org_id = :orgId
            """, nativeQuery = true)
    int updateProjectRow(
            @Param("orgId") Long orgId,
            @Param("projectId") Long projectId,
            @Param("productId") Long productId,
            @Param("projectCode") String projectCode,
            @Param("name") String name,
            @Param("description") String description,
            @Param("status") String status,
            @Param("methodology") String methodology,
            @Param("priority") String priority,
            @Param("totalBacklogPoints") Integer totalBacklogPoints,
            @Param("sprintDurationWeeks") Integer sprintDurationWeeks,
            @Param("targetVelocity") Integer targetVelocity,
            @Param("autoRolloverBacklog") Boolean autoRolloverBacklog,
            @Param("computedSprintCount") Integer computedSprintCount,
            @Param("computedTotalDurationWeeks") Integer computedTotalDurationWeeks
    );

    @Modifying
    @Query(value = "DELETE FROM projects WHERE id = :projectId AND org_id = :orgId", nativeQuery = true)
    int deleteProjectRow(
            @Param("orgId") Long orgId,
            @Param("projectId") Long projectId
    );
}
