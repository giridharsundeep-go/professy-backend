package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.DocAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocAuditLogRepository extends JpaRepository<DocAuditLog, Long> {
    List<DocAuditLog> findAllByOrgId(Long orgId);
    List<DocAuditLog> findTop100ByOrgIdOrderByCreatedAtDesc(Long orgId);
    List<DocAuditLog> findByOrgIdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
            Long orgId, DocAuditLog.EntityType entityType, Long entityId);
    Optional<DocAuditLog> findByIdAndOrgId(Long id, Long orgId);
    Optional<DocAuditLog> findByOrgIdAndId(Long orgId, Long id);
}
