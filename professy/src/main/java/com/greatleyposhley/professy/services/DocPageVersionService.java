package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.DocPageVersion;
import com.greatleyposhley.professy.repositories.DocPageRepository;
import com.greatleyposhley.professy.repositories.DocPageVersionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service @Transactional
public class DocPageVersionService {
 private final com.greatleyposhley.professy.collaboration.DocumentAccess access;
 private final DocPageVersionRepository repository; private final DocPageRepository pages;
 public DocPageVersionService(DocPageVersionRepository repository, DocPageRepository pages, com.greatleyposhley.professy.collaboration.DocumentAccess access) { this.access=access;this.repository=repository; this.pages=pages; }
 @Transactional(readOnly=true) public List<DocPageVersion> getAll(Long orgId) { var allowed=access.readable();return repository.findAllByOrgId(orgId).stream().filter(version->allowed.contains(version.getPageId())).toList(); }
 @Transactional(readOnly=true) public DocPageVersion getById(Long orgId, Long id) { DocPageVersion v=repository.findByOrgIdAndId(orgId,id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Page version not found")); requirePage(orgId,v.getPageId()); return v; }
 public DocPageVersion create(Long orgId, DocPageVersion input) { access.require(orgId,input.getPageId(),true); requirePage(orgId,input.getPageId()); input.setId(null); input.setOrgId(orgId); return repository.save(input); }
 public void delete(Long orgId, Long id) { var version=getById(orgId,id);access.require(orgId,version.getPageId(),true);repository.delete(version); }
 private void requirePage(Long orgId, Long pageId) { access.require(orgId,pageId,false); if(pageId==null || pages.findByOrgIdAndId(orgId,pageId).isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Page not found"); }
}
