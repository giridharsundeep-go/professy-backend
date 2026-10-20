package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Tasks;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {

    List<Tasks> findAllByOrgIdOrderByCreatedAtAsc(Long orgId);

    List<Tasks> findAllByOrgIdAndSprint_IdOrderByCreatedAtAsc(
            Long orgId,
            Long sprintId
    );

    List<Tasks> findAllByOrgIdAndStory_IdOrderByCreatedAtAsc(
            Long orgId,
            Long storyId
    );
}
