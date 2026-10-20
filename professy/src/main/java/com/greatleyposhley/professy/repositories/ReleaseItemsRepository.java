
package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.ReleaseItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReleaseItemsRepository extends JpaRepository<ReleaseItems, Long> {

    List<ReleaseItems> findByOrgIdAndReleaseIdOrderByIncludedAtDesc(
            Long orgId, Long releaseId);

    List<ReleaseItems> findByOrgIdAndSprintIdOrderByIncludedAtDesc(
            Long orgId, Long sprintId);

    List<ReleaseItems> findByOrgIdOrderByIncludedAtDesc(Long orgId);

    List<ReleaseItems> findByOrgIdAndReleaseIdAndItemTypeOrderByIncludedAtDesc(
            Long orgId,
            Long releaseId,
            ReleaseItems.ItemType itemType);

    Optional<ReleaseItems> findByOrgIdAndReleaseIdAndWorkItemIdAndItemType(
            Long orgId,
            Long releaseId,
            Long workItemId,
            ReleaseItems.ItemType itemType);

    boolean existsByOrgIdAndReleaseIdAndWorkItemIdAndItemType(
            Long orgId,
            Long releaseId,
            Long workItemId,
            ReleaseItems.ItemType itemType);

    void deleteByOrgIdAndReleaseId(Long orgId, Long releaseId);

    void deleteByOrgIdAndReleaseIdAndWorkItemIdAndItemType(
            Long orgId,
            Long releaseId,
            Long workItemId,
            ReleaseItems.ItemType itemType);
}
