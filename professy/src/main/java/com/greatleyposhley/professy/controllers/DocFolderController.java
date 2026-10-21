
package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.DocFolder;
import com.greatleyposhley.professy.services.DocFolderService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docs")
public class DocFolderController {

    private final DocFolderService folderService;

    public DocFolderController(DocFolderService folderService) {
        this.folderService = folderService;
    }

    public record CreateFolderRequest(
            String name,
            Long parentFolderId,
            Long createdBy
    ) {}

    public record RenameFolderRequest(
            String name,
            Long updatedBy
    ) {}

    @GetMapping("/spaces/{spaceId}/folders")
    public ResponseEntity<List<DocFolder>> getRootFolders(
            @PathVariable Long spaceId) {

        return ResponseEntity.ok(
                folderService.getRootFolders(spaceId)
        );
    }

    @GetMapping("/folders/{folderId}/children")
    public ResponseEntity<List<DocFolder>> getChildFolders(
            @PathVariable Long folderId) {

        return ResponseEntity.ok(
                folderService.getChildFolders(folderId)
        );
    }

    @PostMapping("/spaces/{spaceId}/folders")
    public ResponseEntity<DocFolder> createFolder(
            @PathVariable Long spaceId,
            @RequestBody CreateFolderRequest request) {

        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Folder name is required"
            );
        }

        if (request.createdBy() == null || request.createdBy() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "createdBy is required"
            );
        }

        DocFolder folder = folderService.createFolder(
                spaceId,
                request.parentFolderId(),
                request.name(),
                request.createdBy()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(folder);
    }

    @PatchMapping("/folders/{folderId}")
    public ResponseEntity<DocFolder> renameFolder(
            @PathVariable Long folderId,
            @RequestBody RenameFolderRequest request) {

        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Folder name is required"
            );
        }

        if (request.updatedBy() == null || request.updatedBy() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "updatedBy is required"
            );
        }

        return ResponseEntity.ok(
                folderService.renameFolder(
                        folderId,
                        request.name(),
                        request.updatedBy()
                )
        );
    }

    @DeleteMapping("/folders/{folderId}")
    public ResponseEntity<Map<String, String>> deleteFolder(
            @PathVariable Long folderId) {

        folderService.deleteFolder(folderId);

        return ResponseEntity.ok(
                Map.of("message", "Folder deleted successfully")
        );
    }
}
