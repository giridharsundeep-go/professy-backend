package com.greatleyposhley.professy.collaboration;

import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static com.greatleyposhley.professy.collaboration.WorkAccess.number;
import static com.greatleyposhley.professy.collaboration.DeliveryRules.*;

/** Desk uses the same organization, permissions, linked issues and agent queue as Astra. */
@RestController @RequestMapping("/api/operations/desk")
public class SupportDesk {
 private final DeliveryOperations operations;
 private final WorkAccess access;
 public SupportDesk(DeliveryOperations operations, WorkAccess access){this.operations=operations;this.access=access;}
 private WorkAccess access(){return access;}
 private long org(){return operations.org();}
 private long actor(){return access().actor().getId();}
 private String name(){var user=access().actor();return (Objects.toString(user.getFirstName(),"")+" "+Objects.toString(user.getLastName(),"")).trim();}
 private void event(long id,String kind,String body){access().jdbc.update("INSERT INTO delivery_support_activity(org_id,case_id,actor_id,actor_name,kind,body) VALUES(?,?,?,?,?,?)",org(),id,actor(),name(),kind,body);}
 private Map<String,Object> row(long id){return operations.supportRow(id);}
 private boolean canManage(Map<String,Object> value){return operations.operator()||number(value.get("assignee_user_id"))==actor();}
 private String select(){return "SELECT s.*,COALESCE(d.category,'INCIDENT') AS category,d.resolution,v.name AS serviceName,i.assignee_user_id,CONCAT_WS(' ',u.first_name,u.last_name) AS ownerName,CONCAT_WS(' ',r.first_name,r.last_name) AS requesterName,a.display_name AS agentName FROM delivery_support s JOIN delivery_services v ON v.id=s.service_id JOIN issues i ON i.id=s.issue_id LEFT JOIN user_account u ON u.id=i.assignee_user_id LEFT JOIN user_account r ON r.id=s.requester_id LEFT JOIN workspace_actors a ON a.id=s.agent_id LEFT JOIN delivery_support_details d ON d.case_id=s.id";}
 @GetMapping("/cases") public Object cases(@RequestParam long projectId,@RequestParam(defaultValue="0")long before,@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="")String state,@RequestParam(defaultValue="")String priority,@RequestParam(defaultValue="")String category,@RequestParam(defaultValue="false")boolean mine){
  access().project(projectId);var args=new ArrayList<Object>(List.of(org(),projectId,operations.operator(),actor(),before,before));
  String where=" WHERE s.org_id=? AND s.project_id=? AND (? OR s.requester_id=?) AND (?=0 OR s.id<?)";
  if(!q.isBlank()){String search="%"+text(q,200).replace("!","!!").replace("%","!%").replace("_","!_")+"%";where+=" AND (s.title LIKE ? ESCAPE '!' OR v.name LIKE ? ESCAPE '!' OR CAST(s.id AS CHAR)=?)";args.add(search);args.add(search);args.add(q.replaceFirst("(?i)^GP-",""));}
  if(!state.isBlank()){where+=" AND s.state=?";args.add(choice(state,Set.of("OPEN","IN_PROGRESS","WAITING","QUEUED","RUNNING","REVIEW_READY","RESOLVED","CLOSED","FAILED")));}
  if(!priority.isBlank()){where+=" AND s.priority=?";args.add(choice(priority,Set.of("LOW","MEDIUM","HIGH","CRITICAL")));}
  if(!category.isBlank()){where+=" AND COALESCE(d.category,'INCIDENT')=?";args.add(choice(category,Set.of("INCIDENT","REQUEST","PROBLEM")));}
  if(mine){where+=" AND s.requester_id=?";args.add(actor());}
  var items=access().jdbc.queryForList(select()+where+" ORDER BY s.id DESC LIMIT 41",args.toArray());boolean more=items.size()>40;if(more)items=items.subList(0,40);
  return Map.of("items",items,"nextBefore",more?items.getLast().get("id"):0);
 }
 @GetMapping("/summary") public Object summary(@RequestParam long projectId){access().project(projectId);return access().jdbc.queryForMap("SELECT COUNT(*) AS total,COALESCE(SUM(state NOT IN ('RESOLVED','CLOSED')),0) AS active,COALESCE(SUM(priority='CRITICAL' AND state NOT IN ('RESOLVED','CLOSED')),0) AS critical,COALESCE(SUM(state IN ('QUEUED','RUNNING','REVIEW_READY')),0) AS agentWork,COALESCE(SUM(state IN ('RESOLVED','CLOSED')),0) AS resolved FROM delivery_support WHERE org_id=? AND project_id=? AND (? OR requester_id=?)",org(),projectId,operations.operator(),actor());}
 @GetMapping("/cases/{id}") public Object detail(@PathVariable long id){row(id);var value=access().jdbc.queryForMap(select()+" WHERE s.id=? AND s.org_id=?",id,org());value.put("canManage",canManage(value)&&access().policy.can(access().actor(),"TICKETS.UPDATE"));value.put("activity",access().jdbc.queryForList("SELECT id,actor_id,actor_name,kind,body,created_at FROM delivery_support_activity WHERE case_id=? AND org_id=? ORDER BY id DESC LIMIT 100",id,org()));value.put("runs",access().jdbc.queryForList("SELECT id,state,title,result_json,attachment_id,created_at,updated_at FROM delivery_jobs WHERE support_id=? AND org_id=? ORDER BY created_at DESC LIMIT 20",id,org()));return value;}
 @PostMapping("/cases") @Transactional public Object create(@RequestBody Map<String,Object> body){access().require("TICKETS.CREATE");String category=choice(body.getOrDefault("category","INCIDENT"),Set.of("INCIDENT","REQUEST","PROBLEM"));var value=(Map<?,?>)operations.raise(body);long id=number(value.get("id"));access().jdbc.update("INSERT INTO delivery_support_details(case_id,category) VALUES(?,?)",id,category);event(id,"CREATED","Request created");return detail(id);}
 @PostMapping("/cases/{id}/comments") @Transactional public Object comment(@PathVariable long id,@RequestBody Map<String,Object> body){row(id);String content=required(body.get("body"),12000);event(id,"COMMENT",content);return detail(id);}
 @PatchMapping("/cases/{id}") @Transactional public Object changeState(@PathVariable long id,@RequestBody Map<String,Object> body){row(id);access().require("TICKETS.UPDATE");var value=access().jdbc.queryForMap(select()+" WHERE s.id=? AND s.org_id=? FOR UPDATE",id,org());if(!canManage(value))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Take ownership before updating this request");
  String state=choice(body.get("state"),Set.of("OPEN","IN_PROGRESS","WAITING","RESOLVED","CLOSED"));if(access().jdbc.queryForObject("SELECT COUNT(*) FROM delivery_jobs WHERE support_id=? AND state IN ('QUEUED','RUNNING','REVIEW_READY')",Integer.class,id)>0)throw new ResponseStatusException(HttpStatus.CONFLICT,"Take human ownership to stop the active agent run first");
  String note=text(body.get("note"),12000);if(Set.of("RESOLVED","CLOSED").contains(state)&&note.isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Add a resolution before resolving or closing a request");
  access().jdbc.update("UPDATE delivery_support SET state=?,updated_at=CURRENT_TIMESTAMP(6) WHERE id=? AND org_id=?",state,id,org());
  access().jdbc.update("INSERT INTO delivery_support_details(case_id,category,resolution) VALUES(?,'INCIDENT',?) ON DUPLICATE KEY UPDATE resolution=VALUES(resolution)",id,note);
  var issueBefore=access().ticket("ISSUE",number(value.get("issue_id")),false);String issueState=Set.of("RESOLVED","CLOSED").contains(state)?"COMPLETED":state.equals("IN_PROGRESS")?"IN_PROGRESS":"TODO";access().jdbc.update("UPDATE issues SET status=? WHERE id=? AND org_id=?",issueState,value.get("issue_id"),org());access().recordTicketChange("ISSUE",number(value.get("issue_id")),"STATUS_CHANGED",Map.of("title",issueBefore.get("title"),"status",issueBefore.get("status")),Map.of("title",issueBefore.get("title"),"status",issueState));
  event(id,"STATUS",value.get("state")+" → "+state+(note.isBlank()?"":"\n"+note));return detail(id);
 }
 @PostMapping("/cases/{id}/ownership") @Transactional public Object ownership(@PathVariable long id){operations.takeOwnership(id);event(id,"OWNERSHIP","Human ownership accepted");return detail(id);}
 @PostMapping("/cases/{id}/dispatch") @Transactional public Object dispatch(@PathVariable long id,@RequestBody Map<String,Object> body){operations.dispatch(id,body);event(id,"DISPATCH","Assigned to agent "+number(body.get("agentId")));return detail(id);}
}
