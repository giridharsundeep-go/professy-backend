package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.DocAuditLog;
import com.greatleyposhley.professy.repositories.DocAuditLogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service @Transactional
public class DocAuditLogService {
 private final com.greatleyposhley.professy.collaboration.DocumentAccess access;
 private final DocAuditLogRepository repository;
 public DocAuditLogService(DocAuditLogRepository repository, com.greatleyposhley.professy.collaboration.DocumentAccess access) {this.access=access; this.repository=repository; }
 @Transactional(readOnly=true) public List<DocAuditLog> getAll(Long orgId) { var allowed=access.readable();return repository.findAllByOrgId(orgId).stream().filter(log->log.getEntityType()!=DocAuditLog.EntityType.PAGE||allowed.contains(log.getEntityId())).filter(log->log.getEntityType()!=DocAuditLog.EntityType.PAGE_VERSION).toList(); }
 @Transactional(readOnly=true) public DocAuditLog getById(Long orgId, Long id) { return getAll(orgId).stream().filter(log->log.getId().equals(id)).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Audit log not found")); }
 /** Trusted internal call only: orgId and actorUserId must come from authenticated context. */
 public DocAuditLog record(Long orgId, Long actorUserId, DocAuditLog input) { if(orgId==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Organization is required"); input.setId(null); input.setOrgId(orgId); input.setActorUserId(actorUserId); return repository.save(input); }
}
