package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Sprints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintsRepository extends CrudRepository<Sprints, Long> {
}
