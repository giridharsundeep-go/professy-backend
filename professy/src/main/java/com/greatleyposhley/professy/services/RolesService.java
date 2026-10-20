
package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Roles;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.RolesRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RolesService {

    private final RolesRepository rolesRepository;
    private final UserAccountRepository userAccountRepository;

    public RolesService(
            RolesRepository rolesRepository,
            UserAccountRepository userAccountRepository) {
        this.rolesRepository = rolesRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public List<Roles> getAllRoles(Long orgId) {
        return rolesRepository.findAllByOrgId(orgId);
    }

    @Transactional(readOnly = true)
    public Roles getRoleById(Long orgId, Long id) {
        return rolesRepository.findByOrgIdAndId(orgId, id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public Roles createRole(Long orgId, Roles role, String userEmail) {
        if (role == null) {
            throw new RuntimeException("Role data is required");
        }

        if (orgId == null) {
            throw new RuntimeException("Organisation is required");
        }

        if (role.getName() == null || role.getName().isBlank()) {
            throw new RuntimeException("Role name is required");
        }

        String roleName = role.getName().trim();

        if (rolesRepository.existsByOrgIdAndName(orgId, roleName)) {
            throw new RuntimeException("Role already exists");
        }

        if (userEmail == null || userEmail.isBlank()) {
            throw new RuntimeException("Authenticated user is required");
        }

        UserAccount userAccount = userAccountRepository
                .findByOrgIdAndEmail(orgId, userEmail.trim().toLowerCase())
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        role.setId(null);
        role.setOrgId(orgId);
        role.setName(roleName);
        role.setUserAccount(userAccount);

        return rolesRepository.save(role);
    }

    public Roles updateRole(Long orgId, Long id, Roles role) {
        if (role == null) {
            throw new RuntimeException("Role data is required");
        }

        if (role.getName() == null || role.getName().isBlank()) {
            throw new RuntimeException("Role name is required");
        }

        Roles existingRole = rolesRepository.findByOrgIdAndId(orgId, id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        String roleName = role.getName().trim();

        boolean duplicate = rolesRepository.existsByOrgIdAndName(orgId, roleName);

        if (duplicate && !existingRole.getName().equalsIgnoreCase(roleName)) {
            throw new RuntimeException("Role already exists");
        }

        existingRole.setName(roleName);

        if (role.getDescription() != null) {
            existingRole.setDescription(role.getDescription());
        }

        // Preserve orgId and userAccount from the existing database record.
        return rolesRepository.save(existingRole);
    }

    public void deleteRole(Long orgId, Long id) {
        Roles existingRole = rolesRepository.findByOrgIdAndId(orgId, id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        rolesRepository.delete(existingRole);
    }
}
