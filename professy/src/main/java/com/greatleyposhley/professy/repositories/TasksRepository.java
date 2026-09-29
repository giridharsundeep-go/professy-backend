package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Tasks;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {

    List<Tasks> findAllByOrderByCreatedAtAsc();

    List<Tasks> findAllBySprint_IdOrderByCreatedAtAsc(Long sprintId);

    List<Tasks> findAllByStory_IdOrderByCreatedAtAsc(Long storyId);
}


