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
    private final org.springframework.security.crypto.password.PasswordEncoder passwords;
    private final com.greatleyposhley.professy.collaboration.PasswordSetupService setup;
    private final com.greatleyposhley.professy.collaboration.WorkAccess access;
    private final com.greatleyposhley.professy.collaboration.OrganizationStructure structure;

    public UserAccountService(UserAccountRepository userAccountRepository, org.springframework.security.crypto.password.PasswordEncoder passwords, com.greatleyposhley.professy.collaboration.PasswordSetupService setup, com.greatleyposhley.professy.collaboration.WorkAccess access, com.greatleyposhley.professy.collaboration.OrganizationStructure structure) {
        this.structure=structure;this.passwords=passwords;this.setup=setup;this.access=access;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public List<UserAccount> getAllUsers(Long orgId) {
        return structure.names(userAccountRepository.findByOrgId(orgId));
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> getUserById(Long orgId, Long userId) {
        return userAccountRepository.findById(userId)
                .filter(user -> orgId.equals(user.getOrgId())).map(user -> structure.names(List.of(user)).getFirst());
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> getUserByEmail(Long orgId, String email) {
        return userAccountRepository.findByOrgIdAndEmail(orgId, email).map(user -> structure.names(List.of(user)).getFirst());
    }

    @Transactional(readOnly = true)
    public List<UserAccount> getActiveUsers(Long orgId) {
        return structure.names(userAccountRepository.findByOrgIdAndIsActive(orgId, true));
    }

    @Transactional(readOnly = true)
    public List<UserAccount> getUsersByOrgRole(Long orgId, OrgRole orgRole) {
        return structure.names(userAccountRepository.findByOrgIdAndOrgRole(orgId, orgRole));
    }

    public UserAccount createUser(UserAccount user) {
        access.require("USERS.CREATE");
        user.setId(null);
        user.setEmail(user.getEmail()==null?null:user.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        if (user.getOrgId() == null) {
            throw new IllegalArgumentException("Organization ID is required");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (userAccountRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if(user.getRoleId()!=null&&access.jdbc.queryForObject("SELECT COUNT(*) FROM roles WHERE id=? AND org_id=?",Integer.class,user.getRoleId(),user.getOrgId())==0)throw new IllegalArgumentException("Choose a designation in your organization");
        structure.validate(user);
        user.setOrgRole(OrgRole.ORG_USER);
        user.setPassword(passwords.encode(java.util.UUID.randomUUID().toString()));
        user.setCredentialVersion(0);
        user.setPasswordSetupRequired(true);
        UserAccount saved=userAccountRepository.save(user);
        setup.queue(saved.getId(),"EMAIL_VERIFICATION");access.policy.event(access.actor(),"USER_UPDATED",saved.getId());
        return structure.names(List.of(saved)).getFirst();
    }

    public UserAccount updateUser(Long orgId, Long userId, UserAccount updates) {
        UserAccount existing = getUserById(orgId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found in this organization"));

        if (updates.isOrganizationAssignmentProvided()) {
            access.require("USERS.UPDATE");
            updates.setOrgId(orgId);structure.validateChanges(updates,existing);
            if(updates.isDepartmentProvided())existing.setDepartmentId(updates.getDepartmentId());if(updates.isFunctionProvided())existing.setFunctionId(updates.getFunctionId());if(updates.isDivisionProvided())existing.setDivisionId(updates.getDivisionId());
        }
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
        if (access.policy.can(access.actor(),"USERS.UPDATE") && updates.getIsActive() != null) {
            existing.setIsActive(updates.getIsActive());
        }
        if (access.policy.can(access.actor(),"USERS.UPDATE") && updates.getRoleId() != null) {
            if(access.jdbc.queryForObject("SELECT COUNT(*) FROM roles WHERE id=? AND org_id=?",Integer.class,updates.getRoleId(),orgId)==0)throw new IllegalArgumentException("Choose a designation in your organization");
            existing.setRoleId(updates.getRoleId());
        }
        if (access.policy.can(access.actor(),"USERS.UPDATE") && updates.getEmployeeIdPrefix() != null) {
            existing.setEmployeeIdPrefix(updates.getEmployeeIdPrefix());
        }
        if (access.policy.can(access.actor(),"USERS.UPDATE") && updates.getEmployeeIdNumber() != null) {
            existing.setEmployeeIdNumber(updates.getEmployeeIdNumber());
        }
        if (access.policy.can(access.actor(),"USERS.UPDATE") && updates.getManagerId() != null) {
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
        var saved=userAccountRepository.save(existing);access.policy.event(access.actor(),"USER_UPDATED",saved.getId());return structure.names(List.of(saved)).getFirst();
    }

    public void deactivateUser(Long orgId, Long userId) {
        UserAccount user = getUserById(orgId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found in this organization"));

        access.require("USERS.DELETE");
        user.setIsActive(false);
        userAccountRepository.save(user);
        access.policy.event(access.actor(),"USER_UPDATED",user.getId());
    }
}
