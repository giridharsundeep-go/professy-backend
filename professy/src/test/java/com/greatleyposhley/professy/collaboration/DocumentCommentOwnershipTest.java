package com.greatleyposhley.professy.collaboration;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
class DocumentCommentOwnershipTest {
 private final JdbcTemplate jdbc=mock(JdbcTemplate.class);
 private final WorkAccess access=spy(new WorkAccess(mock(UserAccountRepository.class),jdbc,mock(WorkspacePolicy.class)));
 private final WorkspaceDocumentsController docs=new WorkspaceDocumentsController(access);
 private void setup(){var actor=new UserAccount();actor.setId(7L);actor.setOrgId(309L);actor.setOrgRole(OrgRole.SUPER_USER);doReturn(actor).when(access).actor();doNothing().when(access).require(anyString());doNothing().when(access).project(anyLong());when(jdbc.queryForList(startsWith("SELECT p.*"),any(Object[].class))).thenReturn(List.of(new HashMap<>(Map.of("id",10L,"project_id",4L,"created_by",7L))));}
 @Test void documentOwnerCannotResolveAnotherAuthorsComment(){setup();assertEquals(403,assertThrows(ResponseStatusException.class,()->docs.resolve(10,11,Map.of("resolved",true))).getStatusCode().value());verify(jdbc).update("UPDATE document_comments SET resolved=? WHERE id=? AND page_id=? AND org_id=? AND author_id=?",true,11L,10L,309L,7L);}
 @Test void authorCanResolveOwnComment(){setup();when(jdbc.update(startsWith("UPDATE document_comments SET resolved"),any(Object[].class))).thenReturn(1);when(jdbc.queryForList(startsWith("SELECT c.id"),any(Object[].class))).thenReturn(List.of());assertDoesNotThrow(()->docs.resolve(10,11,Map.of("resolved",true)));}
}
