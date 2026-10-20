package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.DocSpace;
import com.greatleyposhley.professy.repositories.DocSpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DocSpaceService {

    private final DocSpaceRepository repository;

    public DocSpaceService(DocSpaceRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<DocSpace> getAll(Long orgId) {
        return repository.findByOrgIdOrderByNameAsc(orgId);
    }

    @Transactional(readOnly = true)
    public DocSpace getById(Long orgId, Long id) {
        return repository.findByIdAndOrgId(id, orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Documentation space not found"));
    }

    public DocSpace create(DocSpace space) {
        if (repository.existsByOrgIdAndSlug(space.getOrgId(), space.getSlug())) {
            throw new IllegalArgumentException(
                    "A documentation space with this slug already exists");
        }
        space.setId(null);
        return repository.save(space);
    }

    public DocSpace update(Long orgId, Long id, DocSpace input) {
        DocSpace existing = getById(orgId, id);

        existing.setName(input.getName());
        existing.setSlug(input.getSlug());
        existing.setDescription(input.getDescription());
        existing.setVisibility(input.getVisibility());
        existing.setStatus(input.getStatus());
        existing.setUpdatedBy(input.getUpdatedBy());

        return repository.save(existing);
    }

    public void delete(Long orgId, Long id) {
        DocSpace existing = getById(orgId, id);
        repository.delete(existing);
    }
}