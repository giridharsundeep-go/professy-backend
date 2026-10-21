
package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.DocFolder;
import com.greatleyposhley.professy.entities.DocSpace;
import com.greatleyposhley.professy.repositories.DocFolderRepository;
import com.greatleyposhley.professy.repositories.DocSpaceRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class DocFolderService {

    private final DocFolderRepository folderRepository;
    private final DocSpaceRepository spaceRepository;

    public DocFolderService(
            DocFolderRepository folderRepository,
            DocSpaceRepository spaceRepository) {

        this.folderRepository = folderRepository;
        this.spaceRepository = spaceRepository;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DocFolder> getRootFolders(Long spaceId) {

        ensureSpaceExists(spaceId);

        return folderRepository
                .findByDocSpace_IdAndParentFolderIsNullOrderByNameAsc(
                        spaceId
                );
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DocFolder> getChildFolders(Long folderId) {

        getFolderOrThrow(folderId);

        return folderRepository
                .findByParentFolder_IdOrderByNameAsc(folderId);
    }

    public DocFolder createFolder(
            Long spaceId,
            Long parentFolderId,
            String name,
            Long userId) {

        String normalizedName = validateName(name);

        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid user ID is required to create a folder"
            );
        }

        DocSpace space = ensureSpaceExists(spaceId);
        DocFolder parent = null;

        if (parentFolderId != null) {
            parent = getFolderOrThrow(parentFolderId);

            if (!spaceId.equals(parent.getDocSpace().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Parent folder belongs to a different space"
                );
            }
        }

        ensureUniqueName(spaceId, parentFolderId, normalizedName);

        DocFolder folder = new DocFolder();
        folder.setName(normalizedName);
        folder.setDocSpace(space);
        folder.setParentFolder(parent);
        folder.setCreatedBy(userId);
        folder.setUpdatedBy(userId);

        return folderRepository.save(folder);
    }

    public DocFolder renameFolder(
            Long folderId,
            String name,
            Long userId) {

        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid user ID is required to rename a folder"
            );
        }

        DocFolder folder = getFolderOrThrow(folderId);
        String normalizedName = validateName(name);

        Long parentFolderId = folder.getParentFolder() != null
                ? folder.getParentFolder().getId()
                : null;

        ensureUniqueName(
                folder.getDocSpace().getId(),
                parentFolderId,
                normalizedName,
                folderId
        );

        folder.setName(normalizedName);
        folder.setUpdatedBy(userId);

        return folderRepository.save(folder);
    }

    public void deleteFolder(Long folderId) {

        DocFolder folder = getFolderOrThrow(folderId);

        if (folderRepository.existsByParentFolder_Id(folderId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Delete or move the subfolders before deleting this folder"
            );
        }

        folderRepository.delete(folder);
    }

    private DocFolder getFolderOrThrow(Long folderId) {

        return folderRepository.findById(folderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Folder not found: " + folderId
                ));
    }

    private DocSpace ensureSpaceExists(Long spaceId) {

        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Documentation space not found: " + spaceId
                ));
    }

    private String validateName(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Folder name is required"
            );
        }

        String normalized = name.trim();

        if (normalized.length() > 150) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Folder name cannot exceed 150 characters"
            );
        }

        return normalized;
    }

    private void ensureUniqueName(
            Long spaceId,
            Long parentFolderId,
            String name) {

        ensureUniqueName(spaceId, parentFolderId, name, null);
    }

    private void ensureUniqueName(
            Long spaceId,
            Long parentFolderId,
            String name,
            Long excludedFolderId) {

        boolean exists;

        if (parentFolderId == null) {
            exists = folderRepository
                    .existsByDocSpace_IdAndParentFolderIsNullAndNameIgnoreCase(
                            spaceId,
                            name
                    );
        } else {
            exists = folderRepository
                    .existsByDocSpace_IdAndParentFolder_IdAndNameIgnoreCase(
                            spaceId,
                            parentFolderId,
                            name
                    );
        }

        if (exists) {
            boolean sameFolder = excludedFolderId != null
                    && folderRepository.findById(excludedFolderId)
                    .map(folder ->
                            folder.getName().equalsIgnoreCase(name))
                    .orElse(false);

            if (!sameFolder) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "A folder with this name already exists at this level"
                );
            }
        }
    }
}
