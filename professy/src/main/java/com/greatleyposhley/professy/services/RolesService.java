package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Roles;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.RolesRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesService {

    private final RolesRepository rolesRepository;
    private final UserAccountRepository userAccountRepository;

    public RolesService(
            RolesRepository rolesRepository,
            UserAccountRepository userAccountRepository) {

        this.rolesRepository = rolesRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public List<Roles> getAllRoles() {

        return rolesRepository.findAll();
    }

    public Roles getRoleById(Long id) {

        return rolesRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Role not found")
                );
    }

    public Roles createRole(Roles role, String userEmail) {

        if (role == null) {
            throw new RuntimeException("Role data is required");
        }

        if (role.getName() == null || role.getName().isBlank()) {
            throw new RuntimeException("Role name is required");
        }

        String roleName = role.getName().trim();

        if (rolesRepository.existsByName(roleName)) {
            throw new RuntimeException("Role already exists");
        }

        if (userEmail == null || userEmail.isBlank()) {
            throw new RuntimeException("Authenticated user is required");
        }

        UserAccount userAccount = userAccountRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found")
                );

        /*
         * The userAccount must NOT come from the frontend.
         * It is taken from the authenticated JWT user.
         */
        role.setName(roleName);
        role.setUserAccount(userAccount);

        return rolesRepository.save(role);
    }

    public Roles updateRole(Long id, Roles role) {

        if (role == null) {
            throw new RuntimeException("Role data is required");
        }

        if (role.getName() == null || role.getName().isBlank()) {
            throw new RuntimeException("Role name is required");
        }

        Roles existingRole = rolesRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Role not found")
                );

        String roleName = role.getName().trim();

        if (!existingRole.getName().equalsIgnoreCase(roleName)
                && rolesRepository.existsByName(roleName)) {

            throw new RuntimeException("Role already exists");
        }

        existingRole.setName(roleName);

        if (role.getDescription() != null) {
            existingRole.setDescription(role.getDescription());
        }

        /*
         * Do NOT replace userAccount during update.
         *
         * The original owner/user association remains unchanged.
         */
        return rolesRepository.save(existingRole);
    }

    public void deleteRole(Long id) {

        if (!rolesRepository.existsById(id)) {
            throw new RuntimeException("Role not found");
        }

        rolesRepository.deleteById(id);
    }
}