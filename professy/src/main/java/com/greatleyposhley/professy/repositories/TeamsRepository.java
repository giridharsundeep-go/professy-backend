package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Teams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamsRepository extends JpaRepository<Teams, Long> {

    List<Teams> findAllByOrgIdAndUserAccountId(Long orgId, Long userAccountId);

    Optional<Teams> findByIdAndOrgIdAndUserAccountId(Long id, Long orgId, Long userAccountId);

    boolean existsByOrgIdAndNameAndUserAccountId(Long orgId, String name, Long userAccountId);
}
