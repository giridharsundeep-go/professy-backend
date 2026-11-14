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
class CommentOwnershipTest {
 private final JdbcTemplate jdbc=mock(JdbcTemplate.class);
 private final WorkAccess access=spy(new WorkAccess(mock(UserAccountRepository.class),jdbc,mock(WorkspacePolicy.class)));
 private final TicketCollaborationService service=spy(new TicketCollaborationService(access,JsonMapper.builder().build(),System.getProperty("java.io.tmpdir"),mock(TicketActors.class),mock(WorkspaceAgents.class)));
 private void setup(long author) {
  var actor=new UserAccount();actor.setId(7L);actor.setOrgId(309L);actor.setOrgRole(OrgRole.SUPER_USER);
  doReturn(actor).when(access).actor();doReturn("TASK").when(access).kind(anyString());doReturn(Map.of("work_project_id",4L)).when(access).ticket("TASK",2,false);
  when(jdbc.queryForList(contains("FROM ticket_comments WHERE"),any(Object[].class))).thenReturn(List.of(Map.of("author_id",author,"content","Original")));
  doNothing().when(service).record(anyString(),anyLong(),anyString(),any(),any());
 }
 @Test void evenSuperUserCannotEditAnotherAuthorsComment(){setup(8);var error=assertThrows(ResponseStatusException.class,()->service.changeComment("TASK",2,4,"Changed",false));assertEquals(403,error.getStatusCode().value());verify(jdbc,never()).update(anyString(),any(Object[].class));}
 @Test void evenSuperUserCannotDeleteAnotherAuthorsComment(){setup(8);assertEquals(403,assertThrows(ResponseStatusException.class,()->service.changeComment("TASK",2,4,"",true)).getStatusCode().value());verify(jdbc,never()).update(anyString(),any(Object[].class));}
 @Test void authorCanEditAndDeleteOwnComment(){setup(7);service.changeComment("TASK",2,4,"Changed",false);service.changeComment("TASK",2,4,"",true);verify(jdbc).update("UPDATE ticket_comments SET content=?,updated_at=CURRENT_TIMESTAMP(6) WHERE id=?","Changed",4L);verify(jdbc).update("UPDATE ticket_comments SET deleted_at=CURRENT_TIMESTAMP(6) WHERE id=?",4L);}
}
