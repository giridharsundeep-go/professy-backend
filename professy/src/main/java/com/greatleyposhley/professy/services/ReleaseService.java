package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Releases;
import com.greatleyposhley.professy.repositories.ReleaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReleaseService {

    private final ReleaseRepository releaseRepository;

    public ReleaseService(ReleaseRepository releaseRepository) {
        this.releaseRepository = releaseRepository;
    }

    public Releases create(Long orgId, Releases release) {

        if (orgId == null) {
            throw new IllegalArgumentException("Authenticated orgId is required");
        }

        // Always use the authenticated organisation.
        release.setOrgId(orgId);

        validateRelease(release);

        if (releaseRepository.existsByOrgIdAndProjectIdAndVersionNumber(
                orgId,
                release.getProjectId(),
                release.getVersionNumber())) {

            throw new IllegalArgumentException(
                    "Release version already exists for this project: "
                            + release.getVersionNumber()
            );
        }

        if (release.getStatus() == null) {
            release.setStatus(Releases.Status.PLANNED);
        }

        return releaseRepository.save(release);
    }


    public Releases update(Long orgId, Releases release) {

        Releases existing = getById(orgId);

        validateRelease(release);

        if (releaseRepository
                .existsByOrgIdAndProjectIdAndVersionNumber(
                        orgId,
                        release.getProjectId(),
                        release.getVersionNumber())) {

            throw new IllegalArgumentException(
                    "Release version already exists for this project: "
                            + release.getVersionNumber()
            );
        }

        existing.setOrgId(release.getOrgId());
        existing.setProjectId(release.getProjectId());
        existing.setSprintId(release.getSprintId());
        existing.setUserId(release.getUserId());
        existing.setAssigneeId(release.getAssigneeId());
        existing.setName(release.getName());
        existing.setVersionNumber(release.getVersionNumber());
        existing.setStatus(release.getStatus());
        existing.setReleaseDate(release.getReleaseDate());
        existing.setNotes(release.getNotes());

        return releaseRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Releases getById(Long id) {
        return releaseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Release not found with id: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Releases> getAll() {
        return releaseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Releases> getByOrg(Long orgId) {
        return releaseRepository
                .findByOrgIdOrderByCreatedAtDesc(orgId);
    }

    @Transactional(readOnly = true)
    public List<Releases> getByProject(Long orgId, Long projectId) {
        return releaseRepository
                .findByOrgIdAndProjectIdOrderByCreatedAtDesc(
                        orgId,
                        projectId
                );
    }

    @Transactional(readOnly = true)
    public List<Releases> getByProjectAndSprint(
            Long orgId,
            Long projectId,
            Long sprintId
    ) {
        return releaseRepository
                .findByOrgIdAndProjectIdAndSprintIdOrderByCreatedAtDesc(
                        orgId,
                        projectId,
                        sprintId
                );
    }

    public Releases updateStatus(
            Long id,
            Releases.Status status) {

        Releases release = getById(id);

        release.setStatus(status);

        return releaseRepository.save(release);
    }

    public void delete(Long id) {

        Releases release = getById(id);

        releaseRepository.delete(release);
    }

    private void validateRelease(Releases release) {

        if (release.getOrgId() == null) {
            throw new IllegalArgumentException("orgId is required");
        }

        if (release.getProjectId() == null) {
            throw new IllegalArgumentException("projectId is required");
        }

        if (release.getSprintId() == null) {
            throw new IllegalArgumentException("sprintId is required");
        }

        if (release.getUserId() == null) {
            throw new IllegalArgumentException("userId is required");
        }

        if (release.getAssigneeId() == null) {
            throw new IllegalArgumentException("assigneeId is required");
        }

        if (release.getName() == null ||
                release.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Release name is required"
            );
        }

        if (release.getVersionNumber() == null ||
                release.getVersionNumber().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Version number is required"
            );
        }
    }
}