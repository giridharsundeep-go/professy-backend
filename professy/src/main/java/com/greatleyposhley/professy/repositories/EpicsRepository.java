package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Epics;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpicsRepository extends JpaRepository<Epics, Long> {

    List<Epics> findAllByProject_IdOrderByCreatedAtDesc(Long projectId);
}
