package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Testcases;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestcasesRepository extends JpaRepository<Testcases, Long> {

    @EntityGraph(attributePaths = {"project", "epic", "story", "task", "user", "creator", "issue", "tags"})
    @Query("select distinct t from Testcases t where t.orgId = :orgId " +
            "and (:projectId is null or t.project.id = :projectId) " +
            "and exists (select 1 from Testcases tagged join tagged.tags tag where tagged.id = t.id and tag in :tags)")
    List<Testcases> findByTags(@Param("orgId") Long orgId, @Param("projectId") Long projectId, @Param("tags") List<String> tags);

    @Query("select distinct tag from Testcases t join t.tags tag where t.orgId = :orgId " +
            "and (:projectId is null or t.project.id = :projectId) order by tag")
    List<String> findTags(@Param("orgId") Long orgId, @Param("projectId") Long projectId);

    @EntityGraph(attributePaths = {"project", "epic", "story", "task", "user", "creator"})
    List<Testcases> findAllByOrgId(Long orgId);

    @EntityGraph(attributePaths = {"project", "epic", "story", "task", "user", "creator"})
    Optional<Testcases> findByIdAndOrgId(Long id, Long orgId);

    @EntityGraph(attributePaths = {"project", "epic", "story", "task", "user", "creator"})
    List<Testcases> findByOrgIdAndProjectId(Long orgId, Long projectId);

    Optional<Testcases> findByOrgIdAndTestCaseCode(Long orgId, String testCaseCode);
}
