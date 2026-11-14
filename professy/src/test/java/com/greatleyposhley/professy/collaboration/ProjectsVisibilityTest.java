package com.greatleyposhley.professy.collaboration;
import com.greatleyposhley.professy.entities.*;import com.greatleyposhley.professy.repositories.*;import com.greatleyposhley.professy.services.ProjectsService;
import org.junit.jupiter.api.Test;import java.util.*;import static org.mockito.Mockito.*;import static org.junit.jupiter.api.Assertions.*;
class ProjectsVisibilityTest {
 @Test void organizationMemberCanListProjectsOwnedByOtherUsers(){var projects=mock(ProjectsRepository.class);var users=mock(UserAccountRepository.class);var actor=new UserAccount();actor.setId(3L);actor.setOrgId(7L);when(users.findByOrgIdAndEmail(7L,"member@example.test")).thenReturn(Optional.of(actor));when(projects.findProjectRowsByOrgId(7L)).thenReturn(List.of(Map.of("id",10L,"org_id",7L,"user_id",99L,"name","Shared project")));var service=new ProjectsService(projects,mock(SprintsRepository.class),mock(ProjectTeamsRepository.class),mock(ProjectIndividualMembersRepository.class),mock(TeamsRepository.class),mock(TeamMembersRepository.class),users);assertEquals("Shared project",service.getProjects(7L,"member@example.test").getFirst().get("name"));verify(projects).findProjectRowsByOrgId(7L);}
}
