package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Stories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoriesRepository extends JpaRepository<Stories, Long> {

    List<Stories> findAllByOrgIdAndProject_IdOrderByCreatedAtAsc(
            Long orgId,
            Long projectId
    );

    List<Stories> findAllByOrgIdAndSprint_IdOrderByPriorityDescCreatedAtAsc(
            Long orgId,
            Long sprintId
    );
}
