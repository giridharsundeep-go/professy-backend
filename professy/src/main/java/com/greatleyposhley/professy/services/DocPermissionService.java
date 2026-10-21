package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.DocPermission;
import com.greatleyposhley.professy.repositories.DocPermissionRepository;
import com.greatleyposhley.professy.repositories.DocSpaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service @Transactional
public class DocPermissionService {
 private final DocPermissionRepository repository; private final DocSpaceRepository spaces;
 public DocPermissionService(DocPermissionRepository repository, DocSpaceRepository spaces) { this.repository=repository; this.spaces=spaces; }
 @Transactional(readOnly=true) public List<DocPermission> getAll(Long orgId) { return repository.findAllByOrgId(orgId); }
 @Transactional(readOnly=true) public DocPermission getById(Long orgId, Long id) { return repository.findByOrgIdAndId(orgId,id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Permission not found")); }
 public DocPermission create(Long orgId, DocPermission input) { requireSpace(orgId,input.getSpaceId()); input.setId(null); input.setOrgId(orgId); return repository.save(input); }
 public DocPermission update(Long orgId, Long id, DocPermission input) { DocPermission e=getById(orgId,id); Long sid=input.getSpaceId()!=null?input.getSpaceId():e.getSpaceId(); requireSpace(orgId,sid); e.setSpaceId(sid); e.setUserId(input.getUserId()); return repository.save(e); }
 public void delete(Long orgId, Long id) { repository.delete(getById(orgId,id)); }
 private void requireSpace(Long orgId, Long spaceId) { if(spaceId==null || spaces.findByOrgIdAndId(orgId,spaceId).isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Documentation space not found"); }
}
