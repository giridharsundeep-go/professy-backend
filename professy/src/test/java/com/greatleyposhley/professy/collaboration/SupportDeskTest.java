package com.greatleyposhley.professy.collaboration;
import com.greatleyposhley.professy.entities.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
class SupportDeskTest {
 final JdbcTemplate db=mock(JdbcTemplate.class);final WorkspacePolicy policy=mock(WorkspacePolicy.class);
 final WorkAccess access=spy(new WorkAccess(null,db,policy));
 final DeliveryOperations operations=spy(new DeliveryOperations(access,null,null,null,null,null,"/tmp/desk-test"));
 final SupportDesk desk=new SupportDesk(operations,access);final UserAccount user=new UserAccount();
 SupportDeskTest(){user.setId(7L);user.setOrgId(309L);user.setFirstName("Alex");user.setLastName("Rao");doReturn(user).when(access).actor();doReturn(false).when(operations).operator();}
 @Test void privateCaseCannotBeReadOrCommentedOn(){doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN)).when(operations).supportRow(9);assertThrows(ResponseStatusException.class,()->desk.detail(9));assertThrows(ResponseStatusException.class,()->desk.comment(9,Map.of("body","x")));verifyNoInteractions(db);}
 @Test void commentUsesLoggedInAuthorAndOrganization(){doReturn(new HashMap<>(Map.of("id",9L))).when(operations).supportRow(9);when(db.queryForMap(anyString(),any(Object[].class))).thenReturn(new HashMap<>(Map.of("id",9L)));desk.comment(9,Map.of("body","An update","actorId",999,"orgId",123));verify(db).update(eq("INSERT INTO delivery_support_activity(org_id,case_id,actor_id,actor_name,kind,body) VALUES(?,?,?,?,?,?)"),eq(309L),eq(9L),eq(7L),eq("Alex Rao"),eq("COMMENT"),eq("An update"));}
 @Test void statusChangesRequireAnOperatorOrCurrentOwner(){doReturn(Map.of("id",9L)).when(operations).supportRow(9);when(db.queryForMap(anyString(),any(Object[].class))).thenReturn(new HashMap<>(Map.of("id",9L,"assignee_user_id",8L)));var error=assertThrows(ResponseStatusException.class,()->desk.changeState(9,Map.of("state","RESOLVED","note","fixed")));assertEquals(403,error.getStatusCode().value());verify(db,never()).update(anyString(),any(Object[].class));}
 @Test void resolutionNeedsEvidenceNoteBeforeAnyWrite(){doReturn(Map.of("id",9L)).when(operations).supportRow(9);when(db.queryForMap(anyString(),any(Object[].class))).thenReturn(new HashMap<>(Map.of("id",9L,"assignee_user_id",7L,"state","OPEN")));when(db.queryForObject(anyString(),eq(Integer.class),eq(9L))).thenReturn(0);assertThrows(ResponseStatusException.class,()->desk.changeState(9,Map.of("state","RESOLVED")));verify(db,never()).update(anyString(),any(Object[].class));}
 @Test void queueSearchAndVisibilityAreBoundAsParameters(){doNothing().when(access).project(4);when(db.queryForList(startsWith("SELECT s.*"),any(Object[].class))).thenAnswer(call->{String sql=call.getArgument(0);assertFalse(sql.contains("DROP TABLE"));Object[] args=call.getArguments();assertEquals(309L,args[1]);assertEquals(4L,args[2]);assertEquals(false,args[3]);assertEquals(7L,args[4]);return List.of();});desk.cases(4,0,"' DROP TABLE cases; --","","","",false);}
}
