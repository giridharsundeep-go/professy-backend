package com.greatleyposhley.professy.collaboration;

import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SprintRetrospectivesTest {
 final JdbcTemplate jdbc=mock(JdbcTemplate.class);
 final WorkAccess access=spy(new WorkAccess(mock(UserAccountRepository.class),jdbc,mock(WorkspacePolicy.class)));
 final IssuesRepository issues=mock(IssuesRepository.class);
 final SprintRetrospectives retro=new SprintRetrospectives(access,mock(DeliveryOperations.class),mock(ProjectsRepository.class),issues){
  @Override Map<String,Object> sprint(long id){return Map.of("project_id",11L,"name","Sprint 1");}
  @Override boolean manage(){return false;}
 };
 SprintRetrospectivesTest(){var user=new UserAccount();user.setId(7L);user.setOrgId(309L);user.setOrgRole(OrgRole.ORG_USER);doReturn(user).when(access).actor();when(jdbc.queryForMap(anyString(),eq(1L),eq(309L))).thenReturn(Map.of("state","COLLECTING"));}
 void entry(Map<String,Object> row){when(jdbc.queryForList(anyString(),eq(2L),eq(1L),eq(309L))).thenReturn(List.of(row));}
 @Test void closedSprintFeedbackCannotBeChanged(){when(jdbc.queryForMap(anyString(),eq(1L),eq(309L))).thenReturn(Map.of("state","CLOSED"));assertEquals(409,assertThrows(ResponseStatusException.class,()->retro.vote(1L,2L)).getStatusCode().value());verifyNoInteractions(issues);}
 @Test void ordinaryMemberCannotEditAnotherAuthorsNote(){entry(Map.of("author_id",8L));assertEquals(403,assertThrows(ResponseStatusException.class,()->retro.edit(1L,2L,Map.of("body","Changed"))).getStatusCode().value());verifyNoInteractions(issues);}
 @Test void ordinaryMemberCannotCreateAnotherAuthorsAction(){entry(Map.of("author_id",8L,"category","ACTION"));assertEquals(403,assertThrows(ResponseStatusException.class,()->retro.action(1L,2L)).getStatusCode().value());verifyNoInteractions(issues);}
 @Test void retryReusesTheExistingActionTicket(){entry(Map.of("author_id",7L,"category","ACTION","action_issue_id",99L));assertEquals(Map.of("issueId",99L),retro.action(1L,2L));verifyNoInteractions(issues);}
 @Test void onlyActionEntriesBecomeTickets(){entry(Map.of("author_id",7L,"category","WENT_WELL"));assertThrows(IllegalArgumentException.class,()->retro.action(1L,2L));verifyNoInteractions(issues);}
}
