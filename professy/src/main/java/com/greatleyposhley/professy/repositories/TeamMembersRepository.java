package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.TeamMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMembersRepository extends JpaRepository<TeamMembers, Long> {

    List<TeamMembers> findAllByTeamId(Long teamId);

    void deleteAllByTeamId(Long teamId);

    Optional<TeamMembers> findByTeamIdAndUserId(Long teamId, Long userId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    void deleteByTeamIdAndUserId(Long teamId, Long userId);

    List<TeamMembers> findAllByTeam_Id(Long teamId);

}