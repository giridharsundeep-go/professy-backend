package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.DocService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocServiceRepository extends JpaRepository<DocService, Long> {
    List<DocService> findAllByOrgId(Long orgId);
    List<DocService> findByOrgIdOrderByNameAsc(Long orgId);
    Optional<DocService> findByIdAndOrgId(Long id, Long orgId);
    Optional<DocService> findByOrgIdAndId(Long orgId, Long id);
}
