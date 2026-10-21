package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.DocApiVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocApiVersionRepository extends JpaRepository<DocApiVersion, Long> {
    List<DocApiVersion> findAllByOrgId(Long orgId);
    List<DocApiVersion> findByOrgIdAndServiceIdOrderByCreatedAtDesc(Long orgId, Long serviceId);
    Optional<DocApiVersion> findByIdAndOrgId(Long id, Long orgId);
    Optional<DocApiVersion> findByOrgIdAndId(Long orgId, Long id);
}
