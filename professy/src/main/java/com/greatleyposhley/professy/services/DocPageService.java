package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.DocPage;
import com.greatleyposhley.professy.repositories.DocPageRepository;
import com.greatleyposhley.professy.repositories.DocSpaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class DocPageService {

 private final com.greatleyposhley.professy.collaboration.DocumentAccess access;
 private final DocPageRepository repository;
 private final DocSpaceRepository spaces;

 public DocPageService(DocPageRepository repository, DocSpaceRepository spaces, com.greatleyposhley.professy.collaboration.DocumentAccess access) {
  this.access=access;
  this.repository = repository;
  this.spaces = spaces;
 }

 @Transactional(readOnly = true)
 public List<DocPage> getAll(Long orgId) {
  var allowed=access.readable();return repository.findAllByOrgId(orgId).stream().filter(page->allowed.contains(page.getId())).toList();
 }

 @Transactional(readOnly = true)
 public DocPage getById(Long orgId, Long id) {
  access.require(orgId,id,false);
  return repository.findByOrgIdAndId(orgId, id)
          .orElseThrow(() -> new ResponseStatusException(
                  HttpStatus.NOT_FOUND, "Page not found"));
 }

 public DocPage create(DocPage input) {
  requireSpace(input.getOrgId(),
               input.getSpaceId());
               input.setId(null);
  return repository.save(input);
 }

 public DocPage update(Long orgId, Long id, DocPage input) {
  access.require(orgId,id,true);
  DocPage e = getById(orgId, id);

  Long spaceId = input.getSpaceId() != null
          ? input.getSpaceId()
          : e.getSpaceId();

  requireSpace(orgId, spaceId);

  e.setSpaceId(spaceId);
  e.setTitle(input.getTitle());
  e.setSlug(input.getSlug());
  e.setContent(input.getContent());
  e.setPageType(input.getPageType());
  e.setStatus(input.getStatus());
  e.setParentPageId(input.getParentPageId());

  return repository.save(e);
 }

 public void delete(Long orgId, Long id) {
  access.require(orgId,id,true);
  repository.delete(getById(orgId, id));
 }

 private void requireSpace(Long orgId, Long spaceId) {
  if (spaceId == null
          || spaces.findByOrgIdAndId(orgId, spaceId).isEmpty()) {
   throw new ResponseStatusException(
           HttpStatus.NOT_FOUND, "Documentation space not found");
  }
 }
}