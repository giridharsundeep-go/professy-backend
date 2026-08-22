package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Testcases;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestcasesRepository extends JpaRepository<Testcases, Long> {

    @Override
    @EntityGraph(attributePaths = {"project", "epic", "story", "task", "user", "creator"})
    List<Testcases> findAll();

    @Override
    @EntityGraph(attributePaths = {"project", "epic", "story", "task", "user", "creator"})
    Optional<Testcases> findById(Long id);

    @EntityGraph(attributePaths = {"project", "epic", "story", "task", "user", "creator"})
    List<Testcases> findByProjectId(Long projectId);

    Optional<Testcases> findByTestCaseCode(String testCaseCode);
}