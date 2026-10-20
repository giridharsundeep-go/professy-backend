package com.greatleyposhley.professy.components;

import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class OrgComponent {

    private final UserAccountRepository userAccountRepository;

    public OrgComponent(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public Long getOrgId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentication required");
        }

        String email = authentication.getName();

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Authenticated user is required");
        }

        return userAccountRepository
                .findByEmail(email.trim().toLowerCase())
                .map(UserAccount::getOrgId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Authenticated user not found")
                );
    }

}
