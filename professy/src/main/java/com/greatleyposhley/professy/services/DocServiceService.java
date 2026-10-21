package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.DocService;
import com.greatleyposhley.professy.repositories.DocServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service @Transactional
public class DocServiceService {
 private final DocServiceRepository repository;
 public DocServiceService(DocServiceRepository repository) { this.repository=repository; }
 @Transactional(readOnly=true) public List<DocService> getAll(Long orgId) { return repository.findAllByOrgId(orgId); }
 @Transactional(readOnly=true) public DocService getById(Long orgId, Long id) { return repository.findByOrgIdAndId(orgId,id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Service not found")); }
 public DocService create(DocService input) { if(input.getOrgId()==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Organization is required"); input.setId(null); return repository.save(input); }
 public DocService update(Long orgId, Long id, DocService input) { DocService e=getById(orgId,id); e.setName(input.getName()); e.setSlug(input.getSlug()); e.setDescription(input.getDescription()); e.setServiceType(input.getServiceType()); e.setStatus(input.getStatus()); e.setRepositoryUrl(input.getRepositoryUrl()); e.setBaseUrlDev(input.getBaseUrlDev()); e.setBaseUrlTest(input.getBaseUrlTest()); e.setBaseUrlProd(input.getBaseUrlProd()); return repository.save(e); }
 public void delete(Long orgId, Long id) { repository.delete(getById(orgId,id)); }
}
