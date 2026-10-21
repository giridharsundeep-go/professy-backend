package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.dtos.OrgValidateResponse;
import com.greatleyposhley.professy.entities.dtos.VerifyEmailRequest;
import com.greatleyposhley.professy.entities.dtos.VerifyEmailResponse;
import com.greatleyposhley.professy.entities.Org;
import com.greatleyposhley.professy.repositories.OrgRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OrgService {

    private final OrgRepository orgRepository;
    private final UserAccountRepository userRepository;

    public OrgService(OrgRepository orgRepository, UserAccountRepository userRepository) {
        this.orgRepository = orgRepository;
        this.userRepository = userRepository;
    }

    public OrgValidateResponse validateOrganisation(String slug) {
        Org org = getOrgBySlug(slug);
        return new OrgValidateResponse(true, org.getId(), org.getTitle());
    }

    public Org getOrgBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new IllegalArgumentException("Organisation workspace handle cannot be empty.");
        }

        return orgRepository.findBySlug(slug.trim())
                .orElseThrow(() -> new IllegalArgumentException("Organisation not found with slug: " + slug));
    }

    @Transactional
    public Org createOrganisation(Org request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Organisation request cannot be null"
            );
        }

        if (!StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException(
                    "Organisation title is required"
            );
        }

        if (!StringUtils.hasText(request.getSlug())) {
            throw new IllegalArgumentException(
                    "Organisation slug is required"
            );
        }

        String slug = request.getSlug()
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        if (!StringUtils.hasText(slug)) {
            throw new IllegalArgumentException(
                    "A valid organisation slug is required"
            );
        }

        if (orgRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException(
                    "Organisation slug already exists"
            );
        }

        Org org = new Org();
        org.setTitle(request.getTitle().trim());
        org.setSlug(slug);
        org.setDescription(request.getDescription());
        org.setEmail(request.getEmail());
        org.setPhone(request.getPhone());
        org.setAddress(request.getAddress());

        return orgRepository.save(org);
    }

}