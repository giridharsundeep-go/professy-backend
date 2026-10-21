package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.DocPage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocPageRepository extends JpaRepository<DocPage, Long> {
    List<DocPage> findAllByOrgId(Long orgId);
    List<DocPage> findByOrgIdAndSpaceIdOrderBySortOrderAsc(Long orgId, Long spaceId);
    Optional<DocPage> findByIdAndOrgId(Long id, Long orgId);
    Optional<DocPage> findByOrgIdAndId(Long orgId, Long id);
    List<DocPage> findByOrgIdAndParentPageId(Long orgId, Long parentPageId);
}
