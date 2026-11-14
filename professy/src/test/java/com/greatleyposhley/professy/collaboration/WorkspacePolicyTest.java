package com.greatleyposhley.professy.collaboration;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.*;
class WorkspacePolicyTest {
 @Test void ordinaryMembersCannotAssignRolesEvenWithAnExplicitCrudGrant(){
  var jdbc=mock(JdbcTemplate.class);var users=mock(UserAccountRepository.class);var policy=new WorkspacePolicy(jdbc,new ObjectMapper(),users);
  var actor=new UserAccount();actor.setOrgRole(OrgRole.ORG_USER);actor.setOrgId(7L);actor.setId(1L);
  var error=assertThrows(ResponseStatusException.class,()->policy.assign(actor,2,List.of("MANAGEMENT"),Map.of("USERS.UPDATE",true)));
  assertEquals(403,error.getStatusCode().value());verifyNoInteractions(jdbc,users);
 }
 @Test void superUserCannotAssignAccessAcrossOrganizations(){
  var jdbc=mock(JdbcTemplate.class);var users=mock(UserAccountRepository.class);var policy=new WorkspacePolicy(jdbc,new ObjectMapper(),users);
  var actor=new UserAccount();actor.setOrgRole(OrgRole.SUPER_USER);actor.setOrgId(7L);actor.setId(1L);
  var target=new UserAccount();target.setOrgRole(OrgRole.ORG_USER);target.setOrgId(8L);target.setId(2L);when(users.findById(2L)).thenReturn(Optional.of(target));
  var error=assertThrows(ResponseStatusException.class,()->policy.assign(actor,2,List.of("MANAGEMENT"),Map.of()));
  assertEquals(404,error.getStatusCode().value());verifyNoInteractions(jdbc);
 }
 @Test void allMembersReadCreateTicketsAndReleasesAndMoveTickets(){for(String p:List.of("PROJECTS.READ","USERS.READ","TICKETS.CREATE","TICKETS.UPDATE","TICKETS.MOVE","RELEASES.CREATE"))assertTrue(WorkspacePolicy.resolve(p,Set.of(),Map.of()),p);}
 @Test void managementRequiresRoleOrExplicitGrant(){assertFalse(WorkspacePolicy.resolve("SPRINTS.DELETE",Set.of(),Map.of()));assertTrue(WorkspacePolicy.resolve("SPRINTS.DELETE",Set.of("SPRINTS.DELETE"),Map.of()));assertTrue(WorkspacePolicy.resolve("USERS.CREATE",Set.of(),Map.of("USERS.CREATE",true)));}
 @Test void explicitDenyOverridesBothRolesAndDefaults(){assertFalse(WorkspacePolicy.resolve("TICKETS.MOVE",Set.of("TICKETS.MOVE"),Map.of("TICKETS.MOVE",false)));assertFalse(WorkspacePolicy.resolve("TICKETS.CREATE",Set.of(),Map.of("TICKETS.CREATE",false)));}
 @Test void operationGrantsStayIndependent(){assertTrue(WorkspacePolicy.resolve("TICKETS.MOVE",Set.of(),Map.of("TICKETS.UPDATE",false)));assertFalse(WorkspacePolicy.resolve("TICKETS.UPDATE",Set.of(),Map.of("TICKETS.UPDATE",false)));}
 @Test void statusCodesAreSafeAndBounded(){assertEquals("AWAITING_REVIEW",WorkspacePolicy.code("AWAITING_REVIEW"));assertThrows(ResponseStatusException.class,()->WorkspacePolicy.code("done; drop table"));}
}
