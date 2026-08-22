package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Projects;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectsRepository extends CrudRepository<Projects, Long> {
}
