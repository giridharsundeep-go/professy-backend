package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.ProjectIndividualMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectIndividualMembersRepository extends JpaRepository<ProjectIndividualMembers, Long> {

    @Query(value = "SELECT user_account_id FROM project_individual_members WHERE project_id = :projectId ORDER BY user_account_id", nativeQuery = true)
    List<Long> findUserAccountIdsByProjectId(@Param("projectId") Long projectId);

    @Modifying
    @Query(value = "DELETE FROM project_individual_members WHERE project_id = :projectId", nativeQuery = true)
    int deleteAllForProject(@Param("projectId") Long projectId);

    @Modifying
    @Query(value = "INSERT INTO project_individual_members (project_id, user_account_id, user_id) VALUES (:projectId, :userAccountId, :userId)", nativeQuery = true)
    int insertProjectIndividual(
            @Param("projectId") Long projectId,
            @Param("userAccountId") Long userAccountId,
            @Param("userId") Long userId
    );

    @Modifying
    @Query(value = "DELETE FROM project_individual_members WHERE project_id = :projectId AND user_account_id = :userAccountId", nativeQuery = true)
    int removeProjectIndividual(
            @Param("projectId") Long projectId,
            @Param("userAccountId") Long userAccountId
    );

}
