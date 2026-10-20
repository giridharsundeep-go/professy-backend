package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.components.OrgComponent;
import com.greatleyposhley.professy.entities.ReleaseItems;
import com.greatleyposhley.professy.services.ReleaseItemsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/release-items")
public class ReleaseItemsController {

    private final ReleaseItemsService releaseItemService;
    private final OrgComponent orgComponent;

    public ReleaseItemsController(
            ReleaseItemsService releaseItemService, OrgComponent orgComponent) {
        this.releaseItemService = releaseItemService;
        this.orgComponent = orgComponent;
    }

    @PostMapping("/release/{releaseId}")
    public ResponseEntity<ReleaseItems> addItem(
            @PathVariable Long releaseId,
            @RequestBody ReleaseItems item) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        releaseItemService.addItem(
                                releaseId,
                                item
                        )
                );
    }

    @PostMapping("/release/{releaseId}/bulk")
    public ResponseEntity<List<ReleaseItems>> addItems(
            @PathVariable Long releaseId,
            @RequestBody List<ReleaseItems> items, Authentication authentication) {
        Long orgId = orgComponent.getOrgId(authentication);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        releaseItemService.addItems(
                                orgId,
                                releaseId,
                                items
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReleaseItems> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                releaseItemService.getById(id)
        );
    }

    @GetMapping("/release/{releaseId}")
    public ResponseEntity<List<ReleaseItems>> getByRelease(
            @PathVariable Long releaseId, Authentication authentication) {

        Long orgId = orgComponent.getOrgId(authentication);
        return ResponseEntity.ok(
                releaseItemService.getByRelease(orgId, releaseId)
        );
    }

    @GetMapping("/release/{releaseId}/type/{itemType}")
    public ResponseEntity<List<ReleaseItems>> getByType(
            @PathVariable Long releaseId,
            @PathVariable ReleaseItems.ItemType itemType, Authentication authentication) {
        Long orgId = orgComponent.getOrgId(authentication);
        return ResponseEntity.ok(
                releaseItemService.getByType(
                        orgId,
                        releaseId,
                        itemType
                )
        );
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<List<ReleaseItems>> getBySprint(
            @PathVariable Long sprintId, Authentication authentication) {
        Long orgId = orgComponent.getOrgId(authentication);
        return ResponseEntity.ok(
                releaseItemService.getBySprint(orgId, sprintId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReleaseItems> update(
            @PathVariable Long id,
            @RequestBody ReleaseItems item) {

        return ResponseEntity.ok(
                releaseItemService.updateItem(
                        id,
                        item
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        releaseItemService.removeItem(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/release/{releaseId}/work-item/{workItemId}/type/{itemType}")
    public ResponseEntity<Void> deleteByWorkItem(
            @PathVariable Long releaseId,
            @PathVariable Long workItemId,
            @PathVariable ReleaseItems.ItemType itemType,
            Authentication authentication) {

        Long orgId = orgComponent.getOrgId(authentication);

        releaseItemService.removeItem(
                orgId,
                releaseId,
                workItemId,
                itemType
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/release/{releaseId}/all")
    public ResponseEntity<Void> deleteAll(
            @PathVariable Long releaseId, Authentication authentication) {
        Long orgId = orgComponent.getOrgId(authentication);
        releaseItemService.removeAllFromRelease(orgId, releaseId);
        return ResponseEntity.noContent().build();
    }
}