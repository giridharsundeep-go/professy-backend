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
 private final DocAuditLogRepository repository;
 public DocAuditLogService(DocAuditLogRepository repository) { this.repository=repository; }
 @Transactional(readOnly=true) public List<DocAuditLog> getAll(Long orgId) { return repository.findAllByOrgId(orgId); }
 @Transactional(readOnly=true) public DocAuditLog getById(Long orgId, Long id) { return repository.findByOrgIdAndId(orgId,id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Audit log not found")); }
 /** Trusted internal call only: orgId and actorUserId must come from authenticated context. */
 public DocAuditLog record(Long orgId, Long actorUserId, DocAuditLog input) { if(orgId==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Organization is required"); input.setId(null); input.setOrgId(orgId); input.setActorUserId(actorUserId); return repository.save(input); }
}
