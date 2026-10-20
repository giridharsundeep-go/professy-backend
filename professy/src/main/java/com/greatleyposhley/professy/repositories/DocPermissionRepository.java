package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.DocPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocPermissionRepository
        extends JpaRepository<DocPermission, Long> {
    List<DocPermission> findByOrgIdAndSpaceId(Long orgId, Long spaceId);

    Optional<DocPermission> findByIdAndOrgId(Long id, Long orgId);
}