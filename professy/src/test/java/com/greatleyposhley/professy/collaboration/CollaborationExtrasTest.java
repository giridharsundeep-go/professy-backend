package com.greatleyposhley.professy.collaboration;
import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class CollaborationExtrasTest {
 @Test void cloneUsesCurrentCreatorAndClearsPlacementAndAssignment(){
  var jdbc=mock(JdbcTemplate.class);var policy=mock(WorkspacePolicy.class);var access=spy(new WorkAccess(mock(UserAccountRepository.class),jdbc,policy));var actor=new UserAccount();actor.setId(9L);actor.setOrgId(7L);doReturn(actor).when(access).actor();
  doReturn(new LinkedHashMap<String,Object>(Map.of("id",1L,"name","Source","description","Context","project_id",5L,"work_project_id",5L,"assignee_user_id",40L,"sprint_id",8L,"creator_user_id",2L))).when(access).ticket("EPIC",1,false);
  when(policy.validateStatus(7L,"BACKLOG")).thenReturn("BACKLOG");when(jdbc.queryForObject("SELECT LAST_INSERT_ID()",Long.class)).thenReturn(66L);
  var inserted=new HashMap<String,Object>();doAnswer(call->{String sql=call.getArgument(0);String[] fields=sql.substring(sql.indexOf('(')+1,sql.indexOf(')')).split(",");for(int i=0;i<fields.length;i++)inserted.put(fields[i],call.getArgument(i+1));return 1;}).when(jdbc).update(startsWith("INSERT INTO epics"),any(Object[].class));
  var events=mock(TicketCollaborationService.class);var controller=new CollaborationExtrasController(access,events,mock(WorkspaceDocumentsController.class));var result=(Map<?,?>)controller.clone("EPIC",1);
  assertEquals(66L,result.get("id"));assertEquals(5L,result.get("projectId"));assertEquals("Copy of Source",inserted.get("name"));assertEquals(9L,inserted.get("creator_user_id"));assertEquals(9L,inserted.get("reporter_user_id"));assertEquals(7L,inserted.get("org_id"));assertEquals("Context",inserted.get("description"));assertFalse(inserted.containsKey("sprint_id"));assertFalse(inserted.containsKey("assignee_user_id"));verify(access).require("TICKETS.CREATE");verify(events).record(eq("EPIC"),eq(66L),eq("TICKET_CLONED"),any(),any());
 }
 @Test void cloneStopsBeforeWritingWhenSourceIsUnavailable(){var access=mock(WorkAccess.class);when(access.kind("TASK")).thenReturn("TASK");when(access.ticket("TASK",5,false)).thenThrow(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));var controller=new CollaborationExtrasController(access,mock(TicketCollaborationService.class),mock(WorkspaceDocumentsController.class));assertThrows(org.springframework.web.server.ResponseStatusException.class,()->controller.clone("TASK",5));}
}
