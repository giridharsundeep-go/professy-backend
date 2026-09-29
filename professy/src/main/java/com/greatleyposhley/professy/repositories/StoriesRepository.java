package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Stories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoriesRepository extends JpaRepository<Stories, Long> {

    List<Stories> findAllByProject_IdOrderByCreatedAtAsc(Long projectId);

    List<Stories> findAllBySprint_IdOrderByPriorityDescCreatedAtAsc(Long sprintId);
}


