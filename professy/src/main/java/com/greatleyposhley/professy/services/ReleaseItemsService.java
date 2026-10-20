package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Releases;
import com.greatleyposhley.professy.entities.ReleaseItems;
import com.greatleyposhley.professy.repositories.ReleaseItemsRepository;
import com.greatleyposhley.professy.repositories.ReleaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReleaseItemsService {

    private final ReleaseItemsRepository releaseItemRepository;
    private final ReleaseRepository releaseRepository;

    public ReleaseItemsService(
            ReleaseItemsRepository releaseItemRepository,
            ReleaseRepository releaseRepository) {

        this.releaseItemRepository = releaseItemRepository;
        this.releaseRepository = releaseRepository;
    }

    public ReleaseItems addItem(Long releaseId, ReleaseItems request) {
        Releases release = getRelease(releaseId);

        validateItem(request);

        if (!release.getSprintId().equals(request.getSprintId())) {
            throw new IllegalArgumentException(
                    "Release item sprint does not match release sprint");
        }

        if (!release.getOrgId().equals(request.getOrgId())) {
            throw new IllegalArgumentException(
                    "Release item organization does not match release organization");
        }

        if (releaseItemRepository
                .existsByOrgIdAndReleaseIdAndWorkItemIdAndItemType(
                        request.getOrgId(),
                        releaseId,
                        request.getWorkItemId(),
                        request.getItemType())) {
            throw new IllegalArgumentException(
                    "Work item is already included in this release");
        }

        request.setReleaseId(releaseId);
        return releaseItemRepository.save(request);
    }

    public List<ReleaseItems> addItems(
            Long orgId,
            Long releaseId,
            List<ReleaseItems> items) {

        for (ReleaseItems item : items) {
            addItem(releaseId, item);
        }

        return getByRelease(orgId, releaseId);
    }

    @Transactional(readOnly = true)
    public ReleaseItems getById(Long id) {

        return releaseItemRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Release item not found with id: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<ReleaseItems> getByRelease(Long orgId, Long releaseId) {
        return releaseItemRepository
                .findByOrgIdAndReleaseIdOrderByIncludedAtDesc(orgId, releaseId);
    }

    @Transactional(readOnly = true)
    public List<ReleaseItems> getBySprint(Long orgId, Long sprintId) {
        return releaseItemRepository
                .findByOrgIdAndSprintIdOrderByIncludedAtDesc(orgId, sprintId);
    }

    @Transactional(readOnly = true)
    public List<ReleaseItems> getByType(
            Long orgId,
            Long releaseId,
            ReleaseItems.ItemType itemType) {
        return releaseItemRepository
                .findByOrgIdAndReleaseIdAndItemTypeOrderByIncludedAtDesc(
                        orgId, releaseId, itemType);
    }

    public ReleaseItems updateItem(
            Long id,
            ReleaseItems request) {

        ReleaseItems existing = getById(id);

        existing.setAssigneeId(request.getAssigneeId());

        return releaseItemRepository.save(existing);
    }

    public void removeItem(Long id) {

        ReleaseItems item = getById(id);

        releaseItemRepository.delete(item);
    }

    public void removeItem(
            Long orgId,
            Long releaseId,
            Long workItemId,
            ReleaseItems.ItemType itemType) {
        releaseItemRepository
                .deleteByOrgIdAndReleaseIdAndWorkItemIdAndItemType(
                        orgId, releaseId, workItemId, itemType);
    }

    public void removeAllFromRelease(Long orgId, Long releaseId) {
        releaseItemRepository.deleteByOrgIdAndReleaseId(orgId, releaseId);
    }

    private Releases getRelease(Long releaseId) {

        return releaseRepository.findById(releaseId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Release not found with id: " + releaseId
                        )
                );
    }

    private void validateItem(ReleaseItems item) {

        if (item.getOrgId() == null) {
            throw new IllegalArgumentException("orgId is required");
        }

        if (item.getSprintId() == null) {
            throw new IllegalArgumentException("sprintId is required");
        }

        if (item.getWorkItemId() == null) {
            throw new IllegalArgumentException(
                    "workItemId is required"
            );
        }

        if (item.getItemType() == null) {
            throw new IllegalArgumentException(
                    "itemType is required"
            );
        }
    }
}