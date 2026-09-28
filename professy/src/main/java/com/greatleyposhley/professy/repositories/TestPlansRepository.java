package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.TestPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestPlansRepository extends JpaRepository<TestPlans, Long> {

    List<TestPlans> findByProjectId(Long projectId);

}

