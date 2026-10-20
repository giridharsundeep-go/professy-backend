package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.components.OrgComponent;
import com.greatleyposhley.professy.entities.Releases;
import com.greatleyposhley.professy.repositories.ReleaseRepository;
import com.greatleyposhley.professy.services.ReleaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/releases")
public class ReleaseController {

    private final ReleaseService releaseService;
    private final OrgComponent orgComponent;

    public ReleaseController(ReleaseService releaseService, OrgComponent orgComponent) {
        this.releaseService = releaseService;
        this.orgComponent = orgComponent;
    }

    @PostMapping("/create")
    public ResponseEntity<Releases> create(
            @RequestBody Releases release, Authentication authentication) {
        Long orgId = orgComponent.getOrgId(authentication);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(releaseService.create(orgId, release));
    }

    @GetMapping
    public ResponseEntity<List<Releases>> getAll() {

        return ResponseEntity.ok(
                releaseService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Releases> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                releaseService.getById(id)
        );
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Releases>> getByProject(
            @PathVariable Long projectId, Authentication authentication) {
        Long orgId = orgComponent.getOrgId(authentication);
        return ResponseEntity.ok(
                releaseService.getByProject(orgId, projectId)
        );
    }

    @GetMapping("/project/{projectId}/sprint/{sprintId}")
    public ResponseEntity<List<Releases>> getByProjectAndSprint(
            @PathVariable Long projectId,
            @PathVariable Long sprintId, Authentication authentication) {
        Long orgId = orgComponent.getOrgId(authentication);
        return ResponseEntity.ok(
                releaseService.getByProjectAndSprint(
                        orgId,
                        projectId,
                        sprintId
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Releases> update(
            @PathVariable Long id,
            @RequestBody Releases release) {

        return ResponseEntity.ok(
                releaseService.update(id, release)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Releases> updateStatus(
            @PathVariable Long id,
            @RequestParam Releases.Status status) {

        return ResponseEntity.ok(
                releaseService.updateStatus(id, status)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        releaseService.delete(id);

        return ResponseEntity.noContent().build();
    }
}