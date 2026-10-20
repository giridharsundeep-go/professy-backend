package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.DocSpace;
import com.greatleyposhley.professy.services.DocSpaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docs/spaces")
public class DocSpaceController {

    private final DocSpaceService service;

    public DocSpaceController(DocSpaceService service) {
        this.service = service;
    }

    // Replace this header-based example with your authenticated tenant context.
    @GetMapping
    public List<DocSpace> getAll(
            @RequestHeader("X-Org-Id") Long orgId) {
        return service.getAll(orgId);
    }

    @GetMapping("/{id}")
    public DocSpace getById(
            @RequestHeader("X-Org-Id") Long orgId,
            @PathVariable Long id) {
        return service.getById(orgId, id);
    }

    @PostMapping
    public ResponseEntity<DocSpace> create(
            @RequestHeader("X-Org-Id") Long orgId,
            @RequestBody DocSpace input) {
        input.setOrgId(orgId);
        DocSpace created = service.create(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public DocSpace update(
            @RequestHeader("X-Org-Id") Long orgId,
            @PathVariable Long id,
            @RequestBody DocSpace input) {
        return service.update(orgId, id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-Org-Id") Long orgId,
            @PathVariable Long id) {
        service.delete(orgId, id);
        return ResponseEntity.noContent().build();
    }
}