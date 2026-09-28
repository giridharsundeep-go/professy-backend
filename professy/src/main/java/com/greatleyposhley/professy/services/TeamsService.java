package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.TeamMembers;
import com.greatleyposhley.professy.entities.Teams;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.entities.Users;
import com.greatleyposhley.professy.repositories.TeamMembersRepository;
import com.greatleyposhley.professy.repositories.TeamsRepository;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import com.greatleyposhley.professy.repositories.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeamsService {

    private final TeamsRepository teamsRepository;
    private final TeamMembersRepository teamMembersRepository;
    private final UserAccountRepository userAccountRepository;
    private final UsersRepository usersRepository;

    public TeamsService(
            TeamsRepository teamsRepository,
            TeamMembersRepository teamMembersRepository,
            UserAccountRepository userAccountRepository,
            UsersRepository usersRepository) {

        this.teamsRepository = teamsRepository;
        this.teamMembersRepository = teamMembersRepository;
        this.userAccountRepository = userAccountRepository;
        this.usersRepository = usersRepository;
    }

    // ============================================================
    // CREATE TEAM + MEMBERS
    // ============================================================

    @Transactional
    public Map<String, Object> createTeam(
            String name,
            String description,
            List<Long> memberIds,
            String userEmail) {

        UserAccount userAccount =
                getUserAccount(userEmail);

        validateName(name);

        String teamName = name.trim();

        if (teamsRepository.existsByNameAndUserAccountId(
                teamName,
                userAccount.getId())) {

            throw new RuntimeException(
                    "A team with this name already exists"
            );
        }

        // --------------------------------------------------------
        // 1. CREATE TEAM
        // --------------------------------------------------------

        Teams team = new Teams();

        team.setUserAccount(userAccount);
        team.setName(teamName);
        team.setDescription(
                description == null
                        ? null
                        : description.trim()
        );

        Teams savedTeam =
                teamsRepository.save(team);

        // --------------------------------------------------------
        // 2. CREATE TEAM MEMBERS
        // --------------------------------------------------------

        saveTeamMembers(
                savedTeam,
                memberIds
        );

        // --------------------------------------------------------
        // 3. RETURN TEAM + USERS
        // --------------------------------------------------------

        return buildTeamResponse(
                savedTeam
        );
    }

    // ============================================================
    // GET ALL TEAMS
    //
    // Each team contains:
    //
    // team
    //   └── members: List<Users>
    // ============================================================

    public List<Map<String, Object>> getAllTeams(
            String userEmail) {

        UserAccount userAccount =
                getUserAccount(userEmail);

        List<Teams> teams =
                teamsRepository.findAllByUserAccountId(
                        userAccount.getId()
                );

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (Teams team : teams) {

            result.add(
                    buildTeamResponse(team)
            );
        }

        return result;
    }

    // ============================================================
    // GET ONE TEAM
    // ============================================================

    public Map<String, Object> getTeam(
            Long teamId,
            String userEmail) {

        Teams team =
                getOwnedTeam(
                        teamId,
                        userEmail
                );

        return buildTeamResponse(team);
    }

    // ============================================================
    // UPDATE TEAM + MEMBERS
    //
    // Both tables are updated in ONE transaction.
    // ============================================================

    @Transactional
    public Map<String, Object> updateTeam(
            Long teamId,
            String name,
            String description,
            List<Long> memberIds,
            String userEmail) {

        Teams team =
                getOwnedTeam(
                        teamId,
                        userEmail
                );

        validateName(name);

        String teamName = name.trim();

        // --------------------------------------------------------
        // Check duplicate name
        // --------------------------------------------------------

        if (!team.getName().equalsIgnoreCase(teamName)
                && teamsRepository.existsByNameAndUserAccountId(
                teamName,
                team.getUserAccount().getId())) {

            throw new RuntimeException(
                    "A team with this name already exists"
            );
        }

        // --------------------------------------------------------
        // 1. UPDATE TEAM TABLE
        // --------------------------------------------------------

        team.setName(teamName);

        team.setDescription(
                description == null
                        ? null
                        : description.trim()
        );

        Teams updatedTeam =
                teamsRepository.save(team);

        // --------------------------------------------------------
        // 2. UPDATE TEAM MEMBERS TABLE
        //
        // Replace existing members with supplied user IDs.
        // --------------------------------------------------------

        teamMembersRepository.deleteAllByTeamId(
                teamId
        );

        saveTeamMembers(
                updatedTeam,
                memberIds
        );

        // --------------------------------------------------------
        // 3. RETURN UPDATED TEAM + MEMBERS
        // --------------------------------------------------------

        return buildTeamResponse(
                updatedTeam
        );
    }

    // ============================================================
    // DELETE TEAM
    //
    // Deletes team_members + teams in ONE transaction.
    // ============================================================

    @Transactional
    public void deleteTeam(
            Long teamId,
            String userEmail) {

        Teams team =
                getOwnedTeam(
                        teamId,
                        userEmail
                );

        teamMembersRepository.deleteAllByTeamId(
                teamId
        );

        teamsRepository.delete(team);
    }

    // ============================================================
    // SAVE TEAM MEMBERS
    // ============================================================

    private void saveTeamMembers(
            Teams team,
            List<Long> memberIds) {

        if (memberIds == null ||
                memberIds.isEmpty()) {

            return;
        }

        List<TeamMembers> teamMembers =
                new ArrayList<>();

        for (Long userId : memberIds) {

            if (userId == null) {
                continue;
            }

            Users user =
                    usersRepository
                            .findById(userId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found: "
                                                    + userId
                                    )
                            );

            TeamMembers teamMember =
                    new TeamMembers();

            teamMember.setTeam(team);
            teamMember.setUser(user);

            teamMembers.add(teamMember);
        }

        teamMembersRepository.saveAll(
                teamMembers
        );
    }

    // ============================================================
    // BUILD TEAM RESPONSE
    // ============================================================

    private Map<String, Object> buildTeamResponse(
            Teams team) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "id",
                team.getId()
        );

        response.put(
                "name",
                team.getName()
        );

        response.put(
                "description",
                team.getDescription()
        );

        response.put(
                "createdAt",
                team.getCreatedAt()
        );

        // --------------------------------------------------------
        // Fetch TeamMembers and convert them to Users
        // --------------------------------------------------------

        List<TeamMembers> teamMembers =
                teamMembersRepository.findAllByTeamId(
                        team.getId()
                );

        List<Map<String, Object>> members =
                new ArrayList<>();

        for (TeamMembers teamMember : teamMembers) {

            Users user =
                    teamMember.getUser();

            if (user == null) {
                continue;
            }

            Map<String, Object> member =
                    new LinkedHashMap<>();

            member.put(
                    "id",
                    user.getId()
            );

            member.put(
                    "name",
                    user.getName()
            );

            member.put(
                    "email",
                    user.getEmail()
            );

            member.put(
                    "employeeIdPrefix",
                    user.getEmployeeIdPrefix()
            );

            member.put(
                    "employeeIdNumber",
                    user.getEmployeeIdNumber()
            );

            member.put(
                    "profilePictureUrl",
                    user.getProfilePictureUrl()
            );

            member.put(
                    "isActive",
                    user.getIsActive()
            );

            members.add(member);
        }

        response.put(
                "members",
                members
        );

        return response;
    }

    // ============================================================
    // GET AUTHENTICATED USER ACCOUNT
    // ============================================================

    private UserAccount getUserAccount(
            String userEmail) {

        if (userEmail == null ||
                userEmail.trim().isEmpty()) {

            throw new RuntimeException(
                    "Authenticated user email is required"
            );
        }

        return userAccountRepository
                .findByEmail(
                        userEmail.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        )
                );
    }

    // ============================================================
    // GET OWNED TEAM
    // ============================================================

    private Teams getOwnedTeam(
            Long teamId,
            String userEmail) {

        UserAccount userAccount =
                getUserAccount(userEmail);

        return teamsRepository
                .findByIdAndUserAccountId(
                        teamId,
                        userAccount.getId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Team not found"
                        )
                );
    }

    // ============================================================
    // VALIDATE NAME
    // ============================================================

    private void validateName(
            String name) {

        if (name == null ||
                name.trim().isEmpty()) {

            throw new RuntimeException(
                    "Team name is required"
            );
        }
    }
}