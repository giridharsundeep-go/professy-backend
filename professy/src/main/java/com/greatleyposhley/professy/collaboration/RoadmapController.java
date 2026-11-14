package com.greatleyposhley.professy.collaboration;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.*;
import static com.greatleyposhley.professy.collaboration.WorkAccess.number;

@RestController @RequestMapping("/api/workspace/roadmap")
public class RoadmapController {
 private final WorkAccess access;private final ObjectMapper json;
 public RoadmapController(WorkAccess access,ObjectMapper json){this.access=access;this.json=json;}
 @GetMapping public Object list(@RequestParam long projectId){access.require("PROJECTS.READ");access.project(projectId);return access.jdbc.queryForList("SELECT r.*,CONCAT_WS(' ',u.first_name,u.last_name) AS owner_name FROM roadmap_initiatives r LEFT JOIN user_account u ON u.id=r.owner_id WHERE r.org_id=? AND r.project_id=? ORDER BY r.starts_on,r.id",access.actor().getOrgId(),projectId).stream().map(row->{row.put("tickets",json.readValue(Objects.toString(row.remove("ticket_refs"),"[]"),List.class));return row;}).toList();}
 private Map<String,Object> row(long id){var rows=access.jdbc.queryForList("SELECT * FROM roadmap_initiatives WHERE id=? AND org_id=?",id,access.actor().getOrgId());if(rows.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Initiative not found");var row=rows.getFirst();access.project(number(row.get("project_id")));return row;}
 private List<Object> values(Map<String,Object> body,long project){
  String title=Objects.toString(body.get("title"),"").trim(),description=Objects.toString(body.get("description"),"").trim(),status=Objects.toString(body.get("status"),"PLANNED");if(title.isEmpty()||title.length()>255||description.length()>20000)throw new IllegalArgumentException("Enter a title and a description of up to 20,000 characters");if(!Set.of("PLANNED","IN_PROGRESS","AT_RISK","COMPLETED","PAUSED").contains(status))throw new IllegalArgumentException("Choose an initiative status");
  LocalDate start,end;try{start=LocalDate.parse(Objects.toString(body.get("startsOn"),""));end=LocalDate.parse(Objects.toString(body.get("targetOn"),""));}catch(Exception e){throw new IllegalArgumentException("Choose start and target dates");}if(end.isBefore(start)||end.isAfter(start.plusYears(3)))throw new IllegalArgumentException("Target must be within three years of the start date");
  long owner=number(body.get("ownerId"));if(owner>0&&access.jdbc.queryForObject("SELECT COUNT(*) FROM user_account WHERE id=? AND org_id=? AND is_active=1",Integer.class,owner,access.actor().getOrgId())!=1)throw new IllegalArgumentException("Choose an active owner");
  if(!(body.get("tickets") instanceof List<?> tickets)||tickets.size()>200)throw new IllegalArgumentException("Link up to 200 tickets");var refs=new ArrayList<Map<String,Object>>();var seen=new HashSet<String>();for(Object value:tickets){if(!(value instanceof Map<?,?> ticket))throw new IllegalArgumentException("Choose a ticket");String kind=access.kind(Objects.toString(ticket.get("kind"),""));long id=number(ticket.get("id"));var work=access.ticket(kind,id,false);if(number(work.get("work_project_id"))!=project)throw new IllegalArgumentException("Linked tickets must belong to this project");if(seen.add(kind+id))refs.add(Map.of("kind",kind,"id",id));}
  return Arrays.asList(title,description,owner>0?owner:null,status,start,end,json.writeValueAsString(refs));
 }
 @PostMapping @Transactional public Object create(@RequestBody Map<String,Object> body){access.require("PROJECTS.UPDATE");long project=number(body.get("projectId"));access.project(project);var values=new ArrayList<Object>(Arrays.asList(access.actor().getOrgId(),project));values.addAll(values(body,project));values.add(access.actor().getId());access.jdbc.update("INSERT INTO roadmap_initiatives(org_id,project_id,title,description,owner_id,status,starts_on,target_on,ticket_refs,created_by) VALUES(?,?,?,?,?,?,?,?,?,?)",values.toArray());access.policy.event(access.actor(),"ROADMAP_UPDATED",0);return list(project);}
 @PutMapping("/{id}")@Transactional public Object update(@PathVariable long id,@RequestBody Map<String,Object> body){access.require("PROJECTS.UPDATE");var row=row(id);long project=number(row.get("project_id"));var values=new ArrayList<Object>(values(body,project));values.add(id);values.add(access.actor().getOrgId());values.add(number(body.get("revision")));int changed=access.jdbc.update("UPDATE roadmap_initiatives SET title=?,description=?,owner_id=?,status=?,starts_on=?,target_on=?,ticket_refs=?,revision=revision+1,updated_at=CURRENT_TIMESTAMP WHERE id=? AND org_id=? AND revision=?",values.toArray());if(changed==0)throw new ResponseStatusException(HttpStatus.CONFLICT,"This initiative changed. Reload before editing again.");access.policy.event(access.actor(),"ROADMAP_UPDATED",0);return list(project);}
 @DeleteMapping("/{id}")@Transactional public void delete(@PathVariable long id){access.require("PROJECTS.UPDATE");row(id);access.jdbc.update("DELETE FROM roadmap_initiatives WHERE id=? AND org_id=?",id,access.actor().getOrgId());access.policy.event(access.actor(),"ROADMAP_UPDATED",0);}
}
