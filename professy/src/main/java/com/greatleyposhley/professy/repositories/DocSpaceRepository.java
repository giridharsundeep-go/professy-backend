package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.DocSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocSpaceRepository extends JpaRepository<DocSpace, Long> {
    List<DocSpace> findByOrgIdOrderByNameAsc(Long orgId);
    Optional<DocSpace> findByIdAndOrgId(Long id, Long orgId);
    boolean existsByOrgIdAndSlug(Long orgId, String slug);
}