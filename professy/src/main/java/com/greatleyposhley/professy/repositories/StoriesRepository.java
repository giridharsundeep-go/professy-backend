package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Stories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoriesRepository extends JpaRepository<Stories, Long> {



}
