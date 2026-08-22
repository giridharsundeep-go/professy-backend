package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Epics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpicsRepository extends JpaRepository<Epics, Long> {
}
