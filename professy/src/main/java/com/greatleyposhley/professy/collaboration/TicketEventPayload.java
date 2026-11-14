package com.greatleyposhley.professy.collaboration;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;
@Service
public class TicketEventPayload {
 private final JdbcTemplate jdbc;private final TicketTitles titles;public TicketEventPayload(JdbcTemplate jdbc,TicketTitles titles){this.jdbc=jdbc;this.titles=titles;}
 public Object resolve(String eventId){
  var rows=jdbc.queryForList("SELECT * FROM ticket_activity WHERE event_id=?",eventId);if(rows.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found");var event=rows.getFirst();
  var recipients=jdbc.queryForList("SELECT DISTINCT u.id FROM ticket_watchers w JOIN user_account u ON u.id=w.user_id AND u.org_id=w.org_id WHERE w.org_id=? AND w.ticket_type=? AND w.ticket_id=? AND u.is_active=1 AND u.id<>? AND NOT EXISTS(SELECT 1 FROM user_permission_overrides x WHERE x.org_id=u.org_id AND x.user_id=u.id AND x.permission=? AND x.allowed=0)",Long.class,event.get("org_id"),event.get("ticket_type"),event.get("ticket_id"),event.get("actor_id"),event.get("ticket_type").equals("TEST_CASE")?"TESTCASES.READ":"TICKETS.READ");
  var payload=new LinkedHashMap<String,Object>();payload.put("eventVersion",1);payload.put("occurredAt",event.get("created_at").toString());payload.putAll(Map.of("eventId",eventId,"orgId",event.get("org_id"),"projectId",event.get("project_id"),"ticketType",event.get("ticket_type"),"ticketId",event.get("ticket_id"),"title",titles.title(event.get("ticket_type").toString(),WorkAccess.number(event.get("ticket_id")),WorkAccess.number(event.get("org_id")),event.get("changes_json")),"body",event.get("event_type").toString().replace('_',' '),"recipients",recipients));return payload;
 }
}
