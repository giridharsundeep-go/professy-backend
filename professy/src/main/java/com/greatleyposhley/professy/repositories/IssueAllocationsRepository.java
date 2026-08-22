package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.IssueAllocations;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueAllocationsRepository extends JpaRepository<IssueAllocations, Long> {

    @Transactional
    //@Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "DELETE FROM issue_allocations WHERE issue_id = :issueId", nativeQuery = true)
    void deleteByIssueId(@Param("issueId") Long issueId);

}
