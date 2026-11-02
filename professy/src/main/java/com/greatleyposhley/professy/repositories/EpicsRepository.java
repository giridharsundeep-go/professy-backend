package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Epics;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpicsRepository extends JpaRepository<Epics, Long> {

    // Existing API: retained for callers that still need the complete list.
    List<Epics> findAllByOrgIdAndProject_IdOrderByCreatedAtDesc(
            Long orgId,
            Long projectId
    );

    // Paginated query: Spring Data adds LIMIT/OFFSET and a count query.
    Page<Epics> findAllByOrgIdAndProject_IdOrderByCreatedAtDesc(
            Long orgId,
            Long projectId,
            Pageable pageable
    );
}
