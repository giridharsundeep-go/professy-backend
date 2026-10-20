package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Releases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReleaseRepository extends JpaRepository<Releases, Long> {

    List<Releases> findByOrgIdOrderByCreatedAtDesc(Long orgId);

    List<Releases> findByOrgIdAndProjectIdOrderByCreatedAtDesc(
            Long orgId,
            Long projectId
    );

    List<Releases> findByOrgIdAndProjectIdAndSprintIdOrderByCreatedAtDesc(
            Long orgId,
            Long projectId,
            Long sprintId
    );

    Optional<Releases> findByOrgIdAndId(Long orgId, Long id);

    Optional<Releases> findByOrgIdAndProjectIdAndVersionNumber(
            Long orgId,
            Long projectId,
            String versionNumber
    );

    boolean existsByOrgIdAndProjectIdAndVersionNumber(
            Long orgId,
            Long projectId,
            String versionNumber
    );

    boolean existsByOrgIdAndProjectIdAndVersionNumberAndIdNot(
            Long orgId,
            Long projectId,
            String versionNumber,
            Long id
    );
}
