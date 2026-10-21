package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.DocApiVersion;
import com.greatleyposhley.professy.repositories.DocApiVersionRepository;
import com.greatleyposhley.professy.repositories.DocServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service @Transactional
public class DocApiVersionService {
 private final DocApiVersionRepository repository; private final DocServiceRepository services;
 public DocApiVersionService(DocApiVersionRepository repository, DocServiceRepository services) { this.repository=repository; this.services=services; }
 @Transactional(readOnly=true) public List<DocApiVersion> getAll(Long orgId) { return repository.findAllByOrgId(orgId); }
 @Transactional(readOnly=true) public DocApiVersion getById(Long orgId, Long id) { DocApiVersion v=repository.findByOrgIdAndId(orgId,id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"API version not found")); requireService(orgId,v.getServiceId()); return v; }
 public DocApiVersion create(Long orgId, DocApiVersion input) { requireService(orgId,input.getServiceId()); input.setId(null); input.setOrgId(orgId); return repository.save(input); }
 public DocApiVersion update(Long orgId, Long id, DocApiVersion input) { DocApiVersion e=getById(orgId,id); Long sid=input.getServiceId()!=null?input.getServiceId():e.getServiceId(); requireService(orgId,sid); e.setServiceId(sid); e.setVersionLabel(input.getVersionLabel()); e.setSpecificationFormat(input.getSpecificationFormat()); e.setSpecification(input.getSpecification()); e.setChangelog(input.getChangelog()); e.setStatus(input.getStatus()); e.setDefault(input.getDefault()); return repository.save(e); }
 public void delete(Long orgId, Long id) { repository.delete(getById(orgId,id)); }
 private void requireService(Long orgId, Long serviceId) { if(serviceId==null || services.findByOrgIdAndId(orgId,serviceId).isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Documentation service not found"); }
}
