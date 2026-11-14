package com.greatleyposhley.professy.collaboration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
class WorkAccessTest {
 private final WorkspacePolicy policy=mock(WorkspacePolicy.class);
 private final WorkAccess access=new WorkAccess(mock(UserAccountRepository.class),mock(JdbcTemplate.class),policy);
 private UserAccount user(long id){var u=new UserAccount();u.setId(id);u.setOrgRole(OrgRole.ORG_USER);return u;}
 @Test void editPermissionComesFromCurrentOrganizationPolicy(){var user=user(9);when(policy.can(user,"TICKETS.UPDATE")).thenReturn(true);assertTrue(access.canEdit(user,Map.of("assignee_user_id",1L)));when(policy.can(user,"TICKETS.UPDATE")).thenReturn(false);assertFalse(access.canEdit(user,Map.of("assignee_user_id",9L)));}
 @Test void testcasePermissionIsIndependent(){var user=user(9);when(policy.can(user,"TESTCASES.UPDATE")).thenReturn(true);assertTrue(access.canEdit(user,Map.of("test_case_code","TC-1")));assertFalse(access.canEdit(user,Map.of()));}
 @Test void ticketTableNamesAreWhitelisted(){assertEquals("STORY",access.kind("stories"));assertThrows(ResponseStatusException.class,()->access.kind("epics; DROP TABLE users"));}
}
