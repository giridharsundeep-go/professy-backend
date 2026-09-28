package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.ProjectTeams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectTeamsRepository extends JpaRepository<ProjectTeams, Long> {

    @Query(value = "SELECT team_id FROM project_teams WHERE project_id = :projectId ORDER BY team_id", nativeQuery = true)
    List<Long> findTeamIdsByProjectId(@Param("projectId") Long projectId);

    @Modifying
    @Query(value = "DELETE FROM project_teams WHERE project_id = :projectId", nativeQuery = true)
    int deleteAllForProject(@Param("projectId") Long projectId);

    @Modifying
    @Query(value = "INSERT INTO project_teams (project_id, team_id, user_id) VALUES (:projectId, :teamId, :userId)", nativeQuery = true)
    int insertProjectTeam(
            @Param("projectId") Long projectId,
            @Param("teamId") Long teamId,
            @Param("userId") Long userId
    );

    @Modifying
    @Query(value = "DELETE FROM project_teams WHERE project_id = :projectId AND team_id = :teamId", nativeQuery = true)
    int removeProjectTeam(
            @Param("projectId") Long projectId,
            @Param("teamId") Long teamId
    );

}
