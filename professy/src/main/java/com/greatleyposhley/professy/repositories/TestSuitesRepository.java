package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.TestSuites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSuitesRepository extends JpaRepository<TestSuites, Long> {

    List<TestSuites> findByTestPlanId(Long testPlanId);

    List<TestSuites> findByProjectId(Long projectId);

}
