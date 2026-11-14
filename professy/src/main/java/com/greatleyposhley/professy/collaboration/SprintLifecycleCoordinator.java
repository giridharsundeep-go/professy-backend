package com.greatleyposhley.professy.collaboration;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.ObjectMapper;
import java.time.*;
import java.util.*;

@Service
public class SprintLifecycleCoordinator {
 private static final org.slf4j.Logger log=org.slf4j.LoggerFactory.getLogger(SprintLifecycleCoordinator.class);
 private final JdbcTemplate jdbc;private final TransactionTemplate transactions;private final ObjectMapper json;private final boolean enabled;
 public SprintLifecycleCoordinator(JdbcTemplate jdbc,PlatformTransactionManager manager,ObjectMapper json,@Value("${app.sprint-lifecycle.enabled:true}")boolean enabled){this.jdbc=jdbc;this.transactions=new TransactionTemplate(manager);this.json=json;this.enabled=enabled;}
 public Map<String,Object> sprint(long org,long id){var rows=jdbc.queryForList("SELECT s.*,COALESCE(l.time_zone,'Asia/Kolkata') AS time_zone FROM sprints s LEFT JOIN sprint_lifecycle_settings l ON l.sprint_id=s.id WHERE s.org_id=? AND s.id=?",org,id);if(rows.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Sprint not found");return rows.getFirst();}
 private long number(Object value){return WorkAccess.number(value);}
 private LocalDate date(Object value){return value instanceof java.sql.Date d?d.toLocalDate():LocalDate.parse(value.toString());}
 private boolean complete(String status){return Set.of("COMPLETED","DONE","CLOSED","RESOLVED").contains(String.valueOf(status).toUpperCase(Locale.ROOT).replace(' ','_'));}
 @Transactional public Object settings(long org,long id,long actor,Map<String,Object> body){
  var row=sprint(org,id);lockProject(number(row.get("project_id")));
  if("COMPLETED".equals(row.get("status")))throw new ResponseStatusException(HttpStatus.CONFLICT,"Completed sprint settings cannot be changed");
  String mode=Objects.toString(body.getOrDefault("activationType",row.get("activation_type")));if(!Set.of("AUTOMATIC","MANUAL").contains(mode))throw new IllegalArgumentException("Choose Automatic or Manual");
  String zone=Objects.toString(body.getOrDefault("timeZone",row.get("time_zone")));ZoneId.of(zone);
  LocalDate start=LocalDate.parse(Objects.toString(body.getOrDefault("startDate",row.get("scheduled_start_date"))));LocalDate end=LocalDate.parse(Objects.toString(body.getOrDefault("endDate",row.get("scheduled_end_date"))));
  if(end.isBefore(start))throw new IllegalArgumentException("End date must be on or after start date");
  jdbc.update("UPDATE sprints SET activation_type=?,scheduled_start_date=?,scheduled_end_date=?,updated_at=CURRENT_TIMESTAMP WHERE id=?",mode,start,end,id);
  jdbc.update("INSERT INTO sprint_lifecycle_settings(sprint_id,time_zone,updated_by) VALUES(?,?,?) ON DUPLICATE KEY UPDATE time_zone=VALUES(time_zone),updated_by=VALUES(updated_by)",id,zone,actor);
  audit(org,number(row.get("project_id")),id,actor,"SETTINGS_CHANGED",Map.of("mode",mode,"timeZone",zone,"startDate",start.toString(),"endDate",end.toString()));return sprint(org,id);
 }
 private void lockProject(long project){jdbc.queryForList("SELECT id FROM projects WHERE id=? FOR UPDATE",project);}
 @Transactional public Object start(long org,long id,long actor,boolean automatic){var row=sprint(org,id);long project=number(row.get("project_id"));lockProject(project);row=sprint(org,id);
  if("ACTIVE".equals(row.get("status")))return row;
  if("COMPLETED".equals(row.get("status")))throw new ResponseStatusException(HttpStatus.CONFLICT,"A completed sprint cannot be started again");
  if(!automatic&&!"MANUAL".equals(row.get("activation_type")))throw new ResponseStatusException(HttpStatus.CONFLICT,"Switch this sprint to Manual before starting it manually");
  if(jdbc.queryForObject("SELECT COUNT(*) FROM sprints WHERE project_id=? AND status='ACTIVE' AND id<>?",Integer.class,project,id)>0)throw new ResponseStatusException(HttpStatus.CONFLICT,"Close the active sprint first");
  jdbc.update("UPDATE sprints SET status='ACTIVE',actual_start_at=COALESCE(actual_start_at,CURRENT_TIMESTAMP),updated_at=CURRENT_TIMESTAMP WHERE id=?",id);
  jdbc.update("UPDATE projects SET status='ACTIVE' WHERE id=? AND status='COMPLETED'",project);audit(org,project,id,actor,"SPRINT_STARTED",Map.of("automatic",automatic));return sprint(org,id);
 }
 @Transactional public Object close(long org,long id,long actor,boolean automatic){
  var row=sprint(org,id);long project=number(row.get("project_id"));lockProject(project);row=sprint(org,id);
  if("COMPLETED".equals(row.get("status")))return Map.of("sprint",row,"moved",0);
  if(!automatic&&(!"MANUAL".equals(row.get("activation_type"))||!Set.of("ACTIVE","PAUSED").contains(row.get("status").toString())))throw new ResponseStatusException(HttpStatus.CONFLICT,"Manually start this sprint before closing it");
  int unfinished=unfinishedSprint(org,id);Long next=null;
  if(unfinished>0){
   var nextIds=jdbc.queryForList("SELECT id FROM sprints WHERE org_id=? AND project_id=? AND sprint_number>? AND status<>'COMPLETED' ORDER BY sprint_number LIMIT 1",Long.class,org,project,row.get("sprint_number"));
   next=nextIds.isEmpty()?createNext(row):nextIds.getFirst();
   for(String table:List.of("epics","stories","tasks","issues"))jdbc.update("UPDATE "+table+" SET sprint_id=?,updated_at=CURRENT_TIMESTAMP WHERE org_id=? AND sprint_id=? AND UPPER(REPLACE(status,' ','_')) NOT IN ('COMPLETED','DONE','CLOSED','RESOLVED') AND NOT EXISTS (SELECT 1 FROM workspace_statuses ws WHERE ws.org_id="+table+".org_id AND ws.code="+table+".status AND ws.category='DONE')",next,org,id);
  }
  jdbc.update("UPDATE sprints SET status='COMPLETED',actual_end_at=COALESCE(actual_end_at,CURRENT_TIMESTAMP),updated_at=CURRENT_TIMESTAMP WHERE id=?",id);
  audit(org,project,id,actor,"SPRINT_CLOSED",Map.of("automatic",automatic,"moved",unfinished,"nextSprintId",next==null?0:next));reconcileProject(org,project);return Map.of("sprint",sprint(org,id),"moved",unfinished,"nextSprintId",next==null?0:next);
 }
 private int unfinishedSprint(long org,long sprint){int count=0;for(String table:List.of("epics","stories","tasks","issues"))count+=jdbc.queryForObject("SELECT COUNT(*) FROM "+table+" WHERE org_id=? AND sprint_id=? AND UPPER(REPLACE(status,' ','_')) NOT IN ('COMPLETED','DONE','CLOSED','RESOLVED') AND NOT EXISTS (SELECT 1 FROM workspace_statuses ws WHERE ws.org_id="+table+".org_id AND ws.code="+table+".status AND ws.category='DONE')",Integer.class,org,sprint);return count;}
 private long createNext(Map<String,Object> previous){
  long project=number(previous.get("project_id"));ZoneId zone=ZoneId.of(previous.get("time_zone").toString());LocalDate today=LocalDate.now(zone);LocalDate start=date(previous.get("scheduled_end_date")).plusDays(1);if(start.isBefore(today))start=today;
  int weeks=Math.max(1,(int)number(previous.get("duration_weeks")));int nextNumber=jdbc.queryForObject("SELECT COALESCE(MAX(sprint_number),0)+1 FROM sprints WHERE project_id=?",Integer.class,project);
  String base=previous.get("name").toString().replaceAll("(?i)\\s*\\(Cycle \\d+\\)$","");String name=base.substring(0,Math.min(base.length(),160))+" (Cycle "+nextNumber+")";
  jdbc.update("INSERT INTO sprints(org_id,project_id,user_id,sprint_number,name,status,scheduled_start_date,scheduled_end_date,duration_weeks,target_velocity,activation_type,created_at,updated_at) VALUES(?,?,?,?,?,'PLANNED',?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)",previous.get("org_id"),project,previous.get("user_id"),nextNumber,name,start,start.plusWeeks(weeks).minusDays(1),weeks,previous.get("target_velocity"),previous.get("activation_type"));
  long id=jdbc.queryForObject("SELECT LAST_INSERT_ID()",Long.class);jdbc.update("INSERT INTO sprint_lifecycle_settings(sprint_id,time_zone) VALUES(?,?)",id,zone.getId());return id;
 }
 public void assertMayComplete(long org,long project){if(!mayComplete(org,project))throw new IllegalArgumentException("Complete every sprint and all unfinished project work before marking the project completed");}
 private boolean mayComplete(long org,long project){
  if(jdbc.queryForObject("SELECT COUNT(*) FROM sprints WHERE org_id=? AND project_id=?",Integer.class,org,project)==0)return false;
  if(jdbc.queryForObject("SELECT COUNT(*) FROM sprints WHERE org_id=? AND project_id=? AND status<>'COMPLETED'",Integer.class,org,project)>0)return false;
  for(String table:List.of("epics","stories","issues"))if(jdbc.queryForObject("SELECT COUNT(*) FROM "+table+" WHERE org_id=? AND project_id=? AND UPPER(REPLACE(status,' ','_')) NOT IN ('COMPLETED','DONE','CLOSED','RESOLVED') AND NOT EXISTS (SELECT 1 FROM workspace_statuses ws WHERE ws.org_id="+table+".org_id AND ws.code="+table+".status AND ws.category='DONE')",Integer.class,org,project)>0)return false;
  return jdbc.queryForObject("SELECT COUNT(*) FROM tasks t JOIN stories s ON s.id=t.story_id WHERE t.org_id=? AND s.project_id=? AND UPPER(REPLACE(t.status,' ','_')) NOT IN ('COMPLETED','DONE','CLOSED','RESOLVED') AND NOT EXISTS (SELECT 1 FROM workspace_statuses ws WHERE ws.org_id=t.org_id AND ws.code=t.status AND ws.category='DONE')",Integer.class,org,project)==0;
 }
 private void reconcileProject(long org,long project){if(mayComplete(org,project))jdbc.update("UPDATE projects SET status='COMPLETED' WHERE org_id=? AND id=? AND status='ACTIVE'",org,project);}
 private void audit(long org,long project,long sprint,long actor,String type,Object details){jdbc.update("INSERT INTO sprint_lifecycle_events(org_id,project_id,sprint_id,actor_id,event_type,details) VALUES(?,?,?,?,?,?)",org,project,sprint,actor,type,json.writeValueAsString(details));}
 @Scheduled(fixedDelayString="${app.sprint-lifecycle.delay-ms:60000}")public void processDateWindows(){
  if(!enabled)return;
  for(var project:jdbc.queryForList("SELECT id,org_id FROM projects WHERE status='ACTIVE'")){
   try {transactions.executeWithoutResult(transaction->{
    long id=number(project.get("id")),org=number(project.get("org_id"));if(jdbc.queryForList("SELECT id FROM projects WHERE id=? FOR UPDATE SKIP LOCKED",id).isEmpty())return;
    jdbc.update("SET @professy_actor_id=0");try{
     for(var row:jdbc.queryForList("SELECT s.*,COALESCE(l.time_zone,'Asia/Kolkata') AS time_zone FROM sprints s LEFT JOIN sprint_lifecycle_settings l ON l.sprint_id=s.id WHERE s.org_id=? AND s.project_id=? AND s.activation_type='AUTOMATIC' AND s.status IN ('PLANNED','ACTIVE') ORDER BY s.sprint_number",org,id)){
      LocalDate today=LocalDate.now(ZoneId.of(row.get("time_zone").toString()));long sprint=number(row.get("id"));
      if(today.isAfter(date(row.get("scheduled_end_date"))))close(org,sprint,0,true);
      else if(!today.isBefore(date(row.get("scheduled_start_date")))&&"PLANNED".equals(row.get("status"))&&jdbc.queryForObject("SELECT COUNT(*) FROM sprints WHERE project_id=? AND status='ACTIVE'",Integer.class,id)==0)start(org,sprint,0,true);
     }
     reconcileProject(org,id);
    }finally{jdbc.update("SET @professy_actor_id=NULL");}
   });}catch(Exception error){log.error("Sprint lifecycle failed for project {}",project.get("id"),error);}
  }
 }
}
