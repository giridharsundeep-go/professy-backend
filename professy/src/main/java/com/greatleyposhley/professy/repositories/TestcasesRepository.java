package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Testcases;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestcasesRepository extends JpaRepository<Testcases, Long> {

    @EntityGraph(attributePaths = {"project", "epic", "story", "task", "user", "creator"})
    List<Testcases> findAllByOrgId(Long orgId);

    @EntityGraph(attributePaths = {"project", "epic", "story", "task", "user", "creator"})
    Optional<Testcases> findByIdAndOrgId(Long id, Long orgId);

    @EntityGraph(attributePaths = {"project", "epic", "story", "task", "user", "creator"})
    List<Testcases> findByOrgIdAndProjectId(Long orgId, Long projectId);

    Optional<Testcases> findByOrgIdAndTestCaseCode(Long orgId, String testCaseCode);
}
