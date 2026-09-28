package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Roles;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.entities.Users;
import com.greatleyposhley.professy.repositories.RolesRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import com.greatleyposhley.professy.repositories.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final UserAccountRepository userAccountRepository;
    private final RolesRepository rolesRepository;

    public UsersService(
            UsersRepository usersRepository,
            UserAccountRepository userAccountRepository,
            RolesRepository rolesRepository) {

        this.usersRepository = usersRepository;
        this.userAccountRepository = userAccountRepository;
        this.rolesRepository = rolesRepository;
    }


    public List<Users> getAllUsers() {

        List<Users> users = new ArrayList<>();

        usersRepository.findAll().forEach(users::add);

        return users;
    }

    /*
     * GET USER BY ID
     */
    public Users getUserById(Long id) {

        if (id == null) {
            throw new RuntimeException("User ID is required");
        }

        return usersRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    /*
     * GET USER BY EMAIL
     */
    public Users getUserByEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email address cannot be empty."
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        return usersRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with email: " + email
                        )
                );
    }

    /*
     * CHECK EMAIL EXISTS
     */
    public boolean existsByEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return usersRepository.existsByEmail(
                email.trim().toLowerCase()
        );
    }

    /*
     * CREATE USER
     *
     * userAccount is resolved from the authenticated JWT user.
     * The frontend must NOT send userAccount.
     */
    public Users createUser(
            Users user,
            String authenticatedUserEmail) {

        if (user == null) {
            throw new RuntimeException(
                    "User data is required"
            );
        }

        if (user.getName() == null ||
                user.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "User name is required"
            );
        }

        if (user.getEmail() == null ||
                user.getEmail().trim().isEmpty()) {

            throw new RuntimeException(
                    "User email is required"
            );
        }

        String email =
                user.getEmail().trim().toLowerCase();

        /*
         * Don't allow duplicate application users.
         */
        if (usersRepository.existsByEmail(email)) {
            throw new RuntimeException(
                    "User already exists with email: " + email
            );
        }

        /*
         * Resolve authenticated UserAccount.
         */
        if (authenticatedUserEmail == null ||
                authenticatedUserEmail.trim().isEmpty()) {

            throw new RuntimeException(
                    "Authenticated user is required"
            );
        }

        UserAccount userAccount =
                userAccountRepository
                        .findByEmail(
                                authenticatedUserEmail
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Authenticated user not found"
                                )
                        );


        if (user.getRole() == null) {
            throw new RuntimeException(
                    "Role is required"
            );
        }

        Roles role =
                rolesRepository.findById(user.getRole().getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Role not found"
                                )
                        );

        /*
         * Set normalized values.
         */
        user.setName(user.getName().trim());
        user.setEmail(email);

        /*
         * IMPORTANT:
         * Never accept UserAccount from Angular.
         */
        user.setUserAccount(userAccount);

        /*
         * Associate selected role.
         */
        user.setRole(role);

        return usersRepository.save(user);
    }

    /*
     * UPDATE USER
     */
    public Users updateUser(
            Long id,
            Users user) {

        if (id == null) {
            throw new RuntimeException(
                    "User ID is required"
            );
        }

        if (user == null) {
            throw new RuntimeException(
                    "User data is required"
            );
        }

        Users existingUser =
                usersRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        if (user.getName() == null ||
                user.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "User name is required"
            );
        }

        if (user.getEmail() == null ||
                user.getEmail().trim().isEmpty()) {

            throw new RuntimeException(
                    "User email is required"
            );
        }

        String email =
                user.getEmail().trim().toLowerCase();

        /*
         * Check duplicate email only when email changed.
         */
        if (!existingUser.getEmail()
                .equalsIgnoreCase(email)
                && usersRepository.existsByEmail(email)) {

            throw new RuntimeException(
                    "Another user already exists with email: "
                            + email
            );
        }

        /*
         * Basic information.
         */
        existingUser.setName(
                user.getName().trim()
        );

        existingUser.setEmail(email);

        /*
         * Profile picture.
         */
        if (user.getProfilePictureUrl() != null) {
            existingUser.setProfilePictureUrl(
                    user.getProfilePictureUrl()
            );
        }

        /*
         * Employee information.
         */
        existingUser.setEmployeeIdPrefix(
                user.getEmployeeIdPrefix()
        );

        existingUser.setEmployeeIdNumber(
                user.getEmployeeIdNumber()
        );

        /*
         * Location.
         */
        existingUser.setLocationCountry(
                user.getLocationCountry()
        );

        existingUser.setLocationState(
                user.getLocationState()
        );

        existingUser.setLocationCity(
                user.getLocationCity()
        );

        existingUser.setLocationWorkModel(
                user.getLocationWorkModel()
        );

        existingUser.setLocationDeskCode(
                user.getLocationDeskCode()
        );

        /*
         * Active / inactive state.
         */
        existingUser.setIsActive(
                user.getIsActive()
        );

        /*
         * Manager.
         */
        existingUser.setManager(
                user.getManager()
        );

        /*
         * Role.
         */
        if (user.getRole() != null) {

            Roles role =
                    rolesRepository.findById(
                            user.getRole().getId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Role not found"
                            )
                    );

            existingUser.setRole(role);
        }

        /*
         * IMPORTANT:
         *
         * Do NOT replace existingUser.userAccount here.
         *
         * It belongs to the authenticated account
         * that created the application user.
         */

        return usersRepository.save(existingUser);
    }

    /*
     * DELETE USER
     */
    public void deleteUser(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "User ID is required"
            );
        }

        if (!usersRepository.existsById(id)) {
            throw new RuntimeException(
                    "User not found"
            );
        }

        usersRepository.deleteById(id);
    }
}