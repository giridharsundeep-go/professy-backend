package com.greatleyposhley.professy.collaboration;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
public class ActivityController {
 private final WorkAccess access;private final TicketTitles titles;public ActivityController(WorkAccess access,TicketTitles titles){this.access=access;this.titles=titles;}
 @GetMapping("/api/activity")public Object activity(@RequestParam long projectId,@RequestParam(defaultValue="0")long before){
  if(projectId<=0)return List.of();access.project(projectId);
  var rows=access.jdbc.queryForList("SELECT a.id,a.project_id,a.ticket_type,a.ticket_id,a.event_type,a.summary,a.created_at,a.actor_id,CONCAT_WS(' ',u.first_name,u.last_name) AS actorName FROM ticket_activity a LEFT JOIN user_account u ON u.id=a.actor_id WHERE a.org_id=? AND a.project_id=? AND (?=0 OR a.id<?) ORDER BY a.id DESC LIMIT 50",access.actor().getOrgId(),projectId,before,before);for(var row:rows){row.put("title",titles.title(row.get("ticket_type").toString(),WorkAccess.number(row.get("ticket_id")),access.actor().getOrgId(),null));row.put("summary",row.get("event_type").toString().replace('_',' '));}return rows;
 }
}
