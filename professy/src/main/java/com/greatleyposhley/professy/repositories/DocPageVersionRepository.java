package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.DocPageVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocPageVersionRepository extends JpaRepository<DocPageVersion, Long> {
    List<DocPageVersion> findAllByOrgId(Long orgId);
    List<DocPageVersion> findByOrgIdAndPageIdOrderByVersionNumberDesc(Long orgId, Long pageId);
    Optional<DocPageVersion> findByIdAndOrgId(Long id, Long orgId);
    Optional<DocPageVersion> findByOrgIdAndId(Long orgId, Long id);
}
