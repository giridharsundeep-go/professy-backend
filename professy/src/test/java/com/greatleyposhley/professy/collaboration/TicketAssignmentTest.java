package com.greatleyposhley.professy.collaboration;

import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TicketAssignmentTest {
    private static final String LOCK_TASK =
        "SELECT status,sprint_id,assignee_user_id FROM tasks WHERE id=? AND org_id=? FOR UPDATE";
    private static final String ACTIVE_USER =
        "SELECT COUNT(*) FROM user_account WHERE id=? AND org_id=? AND is_active=1";
    private static final String PROJECT_SPRINT =
        "SELECT COUNT(*) FROM sprints WHERE id=? AND org_id=? AND project_id=?";

    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
    private final WorkAccess access = spy(new WorkAccess(
        mock(UserAccountRepository.class), jdbc, mock(WorkspacePolicy.class)));
    private final TicketActors actors = mock(TicketActors.class);
    private final TicketCollaborationService service = new TicketCollaborationService(
        access, JsonMapper.builder().build(), System.getProperty("java.io.tmpdir"), actors,
        mock(WorkspaceAgents.class));
    private final Map<String, Object> lockedRow = new HashMap<>(Map.of("status", "BACKLOG"));

    @BeforeEach void setup() {
        var actor = new UserAccount();
        actor.setId(7L);
        actor.setOrgId(309L);
        doReturn(actor).when(access).actor();
        doReturn(Map.of("work_project_id", 4L)).when(access).ticket("TASK", 2L, true);
        when(jdbc.queryForList(LOCK_TASK, 2L, 309L)).thenReturn(List.of(lockedRow));
        when(jdbc.queryForObject(ACTIVE_USER, Integer.class, 8L, 309L)).thenReturn(1);
    }

    @Test void changingAssigneeWithoutSprintKeepsBacklogPlacement() {
        service.assignUnassigned("TASK", 2, Map.of("assigneeId", 8));
        verify(actors).finish(List.of(new TicketActors.Change("TASK", 2L, Map.of("assignee", 8L))), new byte[0]);
        verify(access, never()).require("TICKETS.MOVE");
        verify(jdbc, never()).update(anyString(), any(Object[].class));
    }

    @Test void dropOntoSprintChangesPlacementAndAssigns() {
        when(jdbc.queryForObject(PROJECT_SPRINT, Integer.class, 5L, 309L, 4L)).thenReturn(1);
        service.assignUnassigned("TASK", 2, Map.of("assigneeId", 8, "sprintId", 5));
        verify(access).require("TICKETS.MOVE");
        verify(jdbc).update("UPDATE tasks SET status=?,sprint_id=? WHERE id=? AND org_id=?", "TODO", 5L, 2L, 309L);
        verify(actors).finish(List.of(new TicketActors.Change("TASK", 2L, Map.of("assignee", 8L))), new byte[0]);
    }

    @Test void doesNotAssignWhenSprintBelongsToAnotherProject() {
        when(jdbc.queryForObject(PROJECT_SPRINT, Integer.class, 5L, 309L, 4L)).thenReturn(0);
        var error = assertThrows(ResponseStatusException.class, () ->
            service.assignUnassigned("TASK", 2, Map.of("assigneeId", 8, "sprintId", 5)));
        assertEquals(400, error.getStatusCode().value());
        verifyNoInteractions(actors);
        verify(jdbc, never()).update(anyString(), any(Object[].class));
    }

    @Test void doesNotOverwriteAnExistingAssignment() {
        lockedRow.put("assignee_user_id", 11L);
        var error = assertThrows(ResponseStatusException.class, () ->
            service.assignUnassigned("TASK", 2, Map.of("assigneeId", 8)));
        assertEquals(409, error.getStatusCode().value());
        verifyNoInteractions(actors);
        verify(jdbc, never()).update(anyString(), any(Object[].class));
    }
}
