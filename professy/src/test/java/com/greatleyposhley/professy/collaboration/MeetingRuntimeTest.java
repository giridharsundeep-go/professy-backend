package com.greatleyposhley.professy.collaboration;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;
import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
class MeetingRuntimeTest {
 private static final String TOKEN="Bearer gp_meeting_"+"a".repeat(43);
 private final JdbcTemplate jdbc=mock(JdbcTemplate.class);
 private final WorkAccess access=spy(new WorkAccess(mock(UserAccountRepository.class),jdbc,mock(WorkspacePolicy.class)));
 private final MeetingRuntime runtime=new MeetingRuntime(jdbc,access,JsonMapper.builder().build(),"[]");
 private Map<String,Object> session(){return Map.of("meeting_id",4L,"agent_id",9L,"org_id",3L,"display_name","Quality agent");}
 @Test void missingHumanAndMalformedTokensCannotAccessAgentRoom(){
  assertEquals(401,assertThrows(ResponseStatusException.class,()->runtime.get(4,null)).getStatusCode().value());
  assertEquals(401,assertThrows(ResponseStatusException.class,()->runtime.get(4,"Bearer human-jwt")).getStatusCode().value());verifyNoInteractions(jdbc);
 }
 @Test void expiredRevokedPausedAndEndedSessionsFailClosed(){
  when(jdbc.queryForList(contains("workspace_meeting_agent_sessions s"),any(Object[].class))).thenReturn(List.of());
  assertEquals(401,assertThrows(ResponseStatusException.class,()->runtime.signals(4,TOKEN,0)).getStatusCode().value());
  verify(jdbc).queryForList(argThat(sql->sql.contains("expires_at>CURRENT_TIMESTAMP")&&sql.contains("a.status='ACTIVE'")&&sql.contains("m.status NOT IN ('ENDED','CANCELLED')")&&sql.contains("p.participant_id=-a.id")),any(Object[].class));
  verify(jdbc,never()).update(anyString(),any(Object[].class));
 }
 @Test void agentSessionCannotAccessAnotherMeeting(){
  when(jdbc.queryForList(contains("workspace_meeting_agent_sessions s"),any(Object[].class))).thenReturn(List.of(session()));
  assertEquals(401,assertThrows(ResponseStatusException.class,()->runtime.presence(5,TOKEN,Map.of())).getStatusCode().value());verify(jdbc,never()).update(anyString(),any(Object[].class));
 }
 @Test void messagesUseAgentIdentityAndAreScopedToThatRoom(){
  when(jdbc.queryForList(contains("workspace_meeting_agent_sessions s"),any(Object[].class))).thenReturn(List.of(session()));
  when(jdbc.queryForList(contains("SELECT * FROM workspace_meetings"),any(Object[].class))).thenReturn(List.of(Map.of("id",4L)));
  runtime.message(4,TOKEN,Map.of("message","My findings"));
  verify(jdbc).update(startsWith("INSERT INTO workspace_meeting_messages"),eq(4L),eq(-9L),eq("My findings"));
 }
 @Test void onlyInvitedRecipientsCanReceiveSignals(){
  when(jdbc.queryForList(contains("workspace_meeting_agent_sessions s"),any(Object[].class))).thenReturn(List.of(session()));
  when(jdbc.queryForObject(contains("workspace_meeting_members"),eq(Integer.class),any(Object[].class))).thenReturn(0);
  assertThrows(IllegalArgumentException.class,()->runtime.signal(4,TOKEN,Map.of("to",88,"payload",Map.of("type","offer"))));verify(jdbc,never()).update(startsWith("INSERT INTO workspace_meeting_signals"),any(Object[].class));
 }
 @Test void uninvitedAgentOrNonOrganizerCannotIssueCredentials(){
  var user=new UserAccount();user.setId(7L);user.setOrgId(3L);doReturn(user).when(access).actor();doNothing().when(access).require(anyString());
  when(jdbc.queryForList(contains("SELECT m.id"),any(Object[].class))).thenReturn(List.of());
  assertEquals(403,assertThrows(ResponseStatusException.class,()->runtime.issue(4,9)).getStatusCode().value());verify(jdbc,never()).update(anyString(),any(Object[].class));
 }
 @Test void credentialsAreRandomAndOnlyHashesAreStored(){
  var user=new UserAccount();user.setId(7L);user.setOrgId(3L);doReturn(user).when(access).actor();doNothing().when(access).require(anyString());
  when(jdbc.queryForList(contains("SELECT m.id"),any(Object[].class))).thenReturn(List.of(Map.of("id",4L)));
  var first=(Map<?,?>)runtime.issue(4,9);var second=(Map<?,?>)runtime.issue(4,9);
  assertNotEquals(first.get("token"),second.get("token"));assertEquals(7200,first.get("expiresIn"));
  verify(jdbc).update(startsWith("INSERT INTO workspace_meeting_agent_sessions"),eq(MeetingRuntime.digest(first.get("token").toString())),eq(4L),eq(9L),eq(3L),eq(7L));
 }
 @Test void liveHumanPeerCanSignalAnInvitedAgent(){
  var user=new UserAccount();user.setId(7L);user.setOrgId(3L);doReturn(user).when(access).actor();doNothing().when(access).require(anyString());
  when(jdbc.queryForList(contains("FROM workspace_meetings m LEFT JOIN"),any(Object[].class))).thenReturn(List.of(Map.of("id",4L)));
  when(jdbc.queryForObject(contains("workspace_meeting_members"),eq(Integer.class),any(Object[].class))).thenReturn(1);
  new WorkspaceMeetings(access,mock(WorkspaceAgents.class),JsonMapper.builder().build()).signal(4,Map.of("to",-9,"payload",Map.of("type","offer","sdp",Map.of())));
  verify(jdbc).update(startsWith("INSERT INTO workspace_meeting_signals"),eq(4L),eq(7L),eq(-9L),anyString());
 }
}
