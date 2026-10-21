package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.OrgRole;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public List<UserAccount> getAllUsers(Long orgId) {
        return userAccountRepository.findByOrgId(orgId);
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> getUserById(Long orgId, Long userId) {
        return userAccountRepository.findById(userId)
                .filter(user -> orgId.equals(user.getOrgId()));
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> getUserByEmail(Long orgId, String email) {
        return userAccountRepository.findByOrgIdAndEmail(orgId, email);
    }

    @Transactional(readOnly = true)
    public List<UserAccount> getActiveUsers(Long orgId) {
        return userAccountRepository.findByOrgIdAndIsActive(orgId, true);
    }

    @Transactional(readOnly = true)
    public List<UserAccount> getUsersByOrgRole(Long orgId, OrgRole orgRole) {
        return userAccountRepository.findByOrgIdAndOrgRole(orgId, orgRole);
    }

    public UserAccount createUser(UserAccount user) {
        if (user.getOrgId() == null) {
            throw new IllegalArgumentException("Organization ID is required");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (userAccountRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Ordinary user creation must not allow privilege escalation.
        if (user.getOrgRole() == null) {
            user.setOrgRole(OrgRole.ORG_USER);
        }

        return userAccountRepository.save(user);
    }

    public UserAccount updateUser(Long orgId, Long userId, UserAccount updates) {
        UserAccount existing = getUserById(orgId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found in this organization"));

        if (updates.getFirstName() != null) {
            existing.setFirstName(updates.getFirstName());
        }
        if (updates.getLastName() != null) {
            existing.setLastName(updates.getLastName());
        }
        if (updates.getPhone() != null) {
            existing.setPhone(updates.getPhone());
        }
        if (updates.getGender() != null) {
            existing.setGender(updates.getGender());
        }
        if (updates.getIsActive() != null) {
            existing.setIsActive(updates.getIsActive());
        }
        if (updates.getRoleId() != null) {
            existing.setRoleId(updates.getRoleId());
        }
        if (updates.getEmployeeIdPrefix() != null) {
            existing.setEmployeeIdPrefix(updates.getEmployeeIdPrefix());
        }
        if (updates.getEmployeeIdNumber() != null) {
            existing.setEmployeeIdNumber(updates.getEmployeeIdNumber());
        }
        if (updates.getManagerId() != null) {
            existing.setManagerId(updates.getManagerId());
        }
        if (updates.getLocationCountry() != null) {
            existing.setLocationCountry(updates.getLocationCountry());
        }
        if (updates.getLocationState() != null) {
            existing.setLocationState(updates.getLocationState());
        }
        if (updates.getLocationCity() != null) {
            existing.setLocationCity(updates.getLocationCity());
        }
        if (updates.getLocationWorkModel() != null) {
            existing.setLocationWorkModel(updates.getLocationWorkModel());
        }
        if (updates.getLocationDeskCode() != null) {
            existing.setLocationDeskCode(updates.getLocationDeskCode());
        }
        if (updates.getProfilePictureUrl() != null) {
            existing.setProfilePictureUrl(updates.getProfilePictureUrl());
        }

        // Do not change orgId, orgRole, password, or email through this
        // general profile-update method. Handle privileged changes separately.
        return userAccountRepository.save(existing);
    }

    public void deactivateUser(Long orgId, Long userId) {
        UserAccount user = getUserById(orgId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found in this organization"));

        user.setIsActive(false);
        userAccountRepository.save(user);
    }
}
