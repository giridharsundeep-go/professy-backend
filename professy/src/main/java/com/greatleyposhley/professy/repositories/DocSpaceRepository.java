package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.DocSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocSpaceRepository extends JpaRepository<DocSpace, Long> {
    List<DocSpace> findAllByOrgId(Long orgId);
    List<DocSpace> findByOrgIdOrderByNameAsc(Long orgId);
    Optional<DocSpace> findByIdAndOrgId(Long id, Long orgId);
    Optional<DocSpace> findByOrgIdAndId(Long orgId, Long id);
    boolean existsByOrgIdAndSlug(Long orgId, String slug);
}
