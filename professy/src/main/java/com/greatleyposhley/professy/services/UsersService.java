package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Roles;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.entities.Users;
import com.greatleyposhley.professy.repositories.RolesRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import com.greatleyposhley.professy.repositories.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class UsersService {
    private final UsersRepository usersRepository;
    private final UserAccountRepository userAccountRepository;
    private final RolesRepository rolesRepository;

    public UsersService(UsersRepository usersRepository, UserAccountRepository userAccountRepository,
                        RolesRepository rolesRepository) {
        this.usersRepository = usersRepository;
        this.userAccountRepository = userAccountRepository;
        this.rolesRepository = rolesRepository;
    }

    @Transactional(readOnly = true)
    public List<Users> getAllUsers(Long orgId) {
        requireOrgId(orgId);
        return usersRepository.findAllByOrgId(orgId);
    }

    @Transactional(readOnly = true)
    public Users getUserById(Long orgId, Long id) {
        requireOrgId(orgId);
        if (id == null) throw new IllegalArgumentException("User ID is required");
        return usersRepository.findByOrgIdAndId(orgId, id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public Users getUserByEmail(Long orgId, String email) {
        requireOrgId(orgId);
        return usersRepository.findByOrgIdAndEmail(orgId, normalizeEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(Long orgId, String email) {
        requireOrgId(orgId);
        return email != null && !email.trim().isEmpty()
                && usersRepository.existsByOrgIdAndEmail(orgId, normalizeEmail(email));
    }

    public Users createUser(Long orgId, Users user, String authenticatedUserEmail) {
        requireOrgId(orgId);
        validateUser(user);
        String email = normalizeEmail(user.getEmail());
        if (usersRepository.existsByOrgIdAndEmail(orgId, email)) {
            throw new DuplicateResourceException("User already exists with email: " + email);
        }

        UserAccount creator = userAccountRepository.findByOrgIdAndEmail(orgId, normalizeEmail(authenticatedUserEmail))
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found in this organization"));
        Roles role = resolveRole(orgId, user.getRole());

        // Do not trust client-supplied ID, orgId, or userAccount.
        user.setId(null);
        user.setOrgId(orgId);
        user.setName(user.getName().trim());
        user.setEmail(email);
        user.setUserAccount(creator);
        user.setRole(role);
        return usersRepository.save(user);
    }

    public Users updateUser(Long orgId, Long id, Users user) {
        requireOrgId(orgId);
        if (id == null) throw new IllegalArgumentException("User ID is required");
        validateUser(user);
        Users existing = usersRepository.findByOrgIdAndId(orgId, id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String email = normalizeEmail(user.getEmail());

        if (!existing.getEmail().equalsIgnoreCase(email)
                && usersRepository.existsByOrgIdAndEmailAndIdNot(orgId, email, id)) {
            throw new DuplicateResourceException("Another user already exists with email: " + email);
        }

        existing.setName(user.getName().trim());
        existing.setEmail(email);
        if (user.getProfilePictureUrl() != null) existing.setProfilePictureUrl(user.getProfilePictureUrl());
        existing.setEmployeeIdPrefix(user.getEmployeeIdPrefix());
        existing.setEmployeeIdNumber(user.getEmployeeIdNumber());
        existing.setLocationCountry(user.getLocationCountry());
        existing.setLocationState(user.getLocationState());
        existing.setLocationCity(user.getLocationCity());
        existing.setLocationWorkModel(user.getLocationWorkModel());
        existing.setLocationDeskCode(user.getLocationDeskCode());
        existing.setIsActive(user.getIsActive());
        existing.setManager(user.getManager());
        if (user.getRole() != null) existing.setRole(resolveRole(orgId, user.getRole()));
        // Preserve existing orgId and userAccount; never overwrite from request JSON.
        return usersRepository.save(existing);
    }

    public void deleteUser(Long orgId, Long id) {
        requireOrgId(orgId);
        if (id == null) throw new IllegalArgumentException("User ID is required");
        Users existing = usersRepository.findByOrgIdAndId(orgId, id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        usersRepository.delete(existing);
    }

    private Roles resolveRole(Long orgId, Roles requested) {
        if (requested == null || requested.getId() == null) throw new IllegalArgumentException("Role is required");
        return rolesRepository.findByOrgIdAndId(orgId, requested.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found in this organization"));
    }

    private void validateUser(Users user) {
        if (user == null) throw new IllegalArgumentException("User data is required");
        if (user.getName() == null || user.getName().trim().isEmpty()) throw new IllegalArgumentException("User name is required");
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) throw new IllegalArgumentException("User email is required");
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("Email address cannot be empty");
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void requireOrgId(Long orgId) {
        if (orgId == null) throw new IllegalArgumentException("Organization context is required");
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) { super(message); }
    }
    public static class DuplicateResourceException extends RuntimeException {
        public DuplicateResourceException(String message) { super(message); }
    }
}
