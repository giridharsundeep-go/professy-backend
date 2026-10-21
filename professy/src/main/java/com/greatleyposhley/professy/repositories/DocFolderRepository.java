package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.DocFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocFolderRepository
        extends JpaRepository<DocFolder, Long> {

    List<DocFolder> findByDocSpace_IdAndParentFolderIsNullOrderByNameAsc(
            Long spaceId
    );

    List<DocFolder> findByParentFolder_IdOrderByNameAsc(
            Long parentFolderId
    );

    List<DocFolder> findByParentFolder_Id(Long parentFolderId);

    Optional<DocFolder> findByIdAndDocSpace_Id(
            Long folderId,
            Long spaceId
    );

    boolean existsByDocSpace_IdAndParentFolder_IdAndNameIgnoreCase(
            Long spaceId,
            Long parentFolderId,
            String name
    );

    boolean existsByDocSpace_IdAndParentFolderIsNullAndNameIgnoreCase(
            Long spaceId,
            String name
    );

    boolean existsByParentFolder_Id(Long parentFolderId);
}