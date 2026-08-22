package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.dtos.OrgValidateResponse;
import com.greatleyposhley.professy.dtos.VerifyEmailRequest;
import com.greatleyposhley.professy.dtos.VerifyEmailResponse;
import com.greatleyposhley.professy.entities.Org;
import com.greatleyposhley.professy.repositories.OrgRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;

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
}