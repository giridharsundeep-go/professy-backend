package com.greatleyposhley.professy.collaboration;

import java.util.*;
import java.time.*;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;
import static com.greatleyposhley.professy.collaboration.DeliveryRules.*;
import static com.greatleyposhley.professy.collaboration.WorkAccess.number;

/** Supported capabilities are explicit. A selected skill is not evidence of installation. */
public final class RuntimeSkills {
 private RuntimeSkills() {}
 public static final List<Map<String,String>> CATALOG=List.of(
  skill("repository-snapshot","Repository work","Built-in frozen snapshot reader and bounded source editor; upgrade the local worker to enable."),
  skill("regression-review","Regression evidence","Built-in baseline, immutable existing tests, new regression tests and unmerged pull request evidence."),
  skill("python-stdlib","Python runtime","Install Python 3 on the worker host; the worker reports its detected version."),
  skill("unittest","Python unittest","Included with Python; no pip package is required."),
  skill("node-runtime","Node runtime","Install Node 22+ at /opt/homebrew/bin/node on the worker host."),
  skill("node-test","Node test runner","Node 22+ built-in runner with test isolation disabled; no npm dependencies are installed."),
  skill("architecture-documentation","Architecture evidence","Built-in architecture and service context capture for the ticket evidence bundle.")
 );
 static Map<String,String> skill(String id,String name,String setup){return Map.of("id",id,"name",name,"setup",setup);}
 public static final Set<String> IDS=new HashSet<>(CATALOG.stream().map(s->s.get("id")).toList());
 public static List<String> selected(Object value){
  if(!(value instanceof List<?> list)||list.size()>IDS.size()||list.stream().anyMatch(s->!(s instanceof String)||!IDS.contains(s)))throw new IllegalArgumentException("Choose supported runtime skills");
  return list.stream().map(Object::toString).distinct().sorted().toList();
 }
 public static List<String> required(Object value,String profile){
  var required=new TreeSet<>(selected(value==null?List.of():value));
  required.addAll(List.of("repository-snapshot","regression-review"));
  required.addAll("NODE_TEST".equals(profile)?List.of("node-runtime","node-test"):List.of("python-stdlib","unittest"));
  return List.copyOf(required);
 }
 public static Map<String,Object> defaults(JdbcTemplate jdbc,ObjectMapper json,long org,long agent){
  var rows=jdbc.queryForList("SELECT defaults_json FROM agent_execution_defaults WHERE org_id=? AND agent_id=?",org,agent);
  return rows.isEmpty()?Map.of():json.readValue(rows.getFirst().get("defaults_json").toString(),Map.class);
 }
 public static Map<String,Object> validateReport(Map<?,?> raw){
  if(!(raw.get("skills") instanceof List<?> skills)||skills.size()>IDS.size())throw new IllegalArgumentException("Invalid worker capabilities report");
  var clean=new ArrayList<Map<String,Object>>();var seen=new HashSet<String>();
  for(var value:skills){if(!(value instanceof Map<?,?> s)||!IDS.contains(s.get("id"))||!seen.add(s.get("id").toString()))throw new IllegalArgumentException("Invalid worker skill");clean.add(Map.of("id",s.get("id"),"available",Boolean.TRUE.equals(s.get("available")),"version",text(s.get("version"),120)));}
  var models=raw.get("availableModels") instanceof List<?> list?list.stream().limit(100).map(v->text(v,120)).toList():List.of();
  return Map.of("availableModels",models,"skills",clean,"runtimeVersion",DeliveryRules.required(raw.get("runtimeVersion"),80),"model",text(raw.get("model"),120),"modelAvailable",Boolean.TRUE.equals(raw.get("modelAvailable")));
 }
 public static Map<String,Object> report(JdbcTemplate jdbc,ObjectMapper json,long org,long agent){
  var rows=jdbc.queryForList("SELECT report_json,reported_at,worker_id FROM delivery_worker_reports WHERE org_id=? AND agent_id=?",org,agent);
  if(rows.isEmpty())return Map.of("online",false,"skills",List.of());var row=rows.getFirst();var result=new LinkedHashMap<String,Object>(json.readValue(row.get("report_json").toString(),Map.class));
  Instant at=row.get("reported_at") instanceof java.sql.Timestamp t?t.toInstant():((java.time.LocalDateTime)row.get("reported_at")).toInstant(ZoneOffset.UTC);
  result.put("online",at.isAfter(Instant.now().minusSeconds(120)));result.put("reportedAt",at.toString());result.put("workerId",row.get("worker_id"));return result;
 }
 public static Map<String,Object> readiness(Map<String,Object> config,Map<String,Object> report,List<String> required,ObjectMapper json){
  List<?> configured=config.get("skills_json")==null?List.of():json.readValue(config.get("skills_json").toString(),List.class);
  var installed=new HashSet<String>();for(var item:(List<?>)report.getOrDefault("skills",List.of())){var s=(Map<?,?>)item;if(Boolean.TRUE.equals(s.get("available")))installed.add(s.get("id").toString());}
  var missingEnabled=required.stream().filter(s->!configured.contains(s)).toList();var missingInstalled=required.stream().filter(s->!installed.contains(s)).toList();
  var reasons=new ArrayList<String>();if(!Boolean.TRUE.equals(config.get("enabled"))&&number(config.get("enabled"))!=1)reasons.add("Enable this agent's worker");if(config.get("worker_hash")==null)reasons.add("Enroll the local worker");if(!Boolean.TRUE.equals(report.get("online")))reasons.add("Worker offline or capability report older than two minutes");if(!missingEnabled.isEmpty())reasons.add("Enable skills: "+String.join(", ",missingEnabled));if(!missingInstalled.isEmpty())reasons.add("Install or update worker capabilities: "+String.join(", ",missingInstalled));String model=Objects.toString(config.get("model"),"");if(model.isBlank())reasons.add("Choose an Ollama model for this agent");else if(!((List<?>)report.getOrDefault("availableModels",List.of())).contains(model)&&(!Boolean.TRUE.equals(report.get("modelAvailable"))||!Objects.equals(model,report.get("model"))))reasons.add("Configured Ollama model is unavailable on this worker");
  return Map.of("ready",reasons.isEmpty(),"reasons",reasons,"missingEnabled",missingEnabled,"missingInstalled",missingInstalled,"requiredSkills",required,"worker",report);
 }
 public static List<Map<String,Object>> events(Object value){
  if(value==null)return List.of();if(!(value instanceof List<?> list)||list.size()>50)throw new IllegalArgumentException("Send at most 50 execution events at a time");
  var clean=new ArrayList<Map<String,Object>>();for(var valueItem:list){if(!(valueItem instanceof Map<?,?> e))throw new IllegalArgumentException("Invalid execution event");long sequence=number(e.get("sequence"));String stage=DeliveryRules.required(e.get("event"),80);if(sequence<1||!stage.matches("[A-Z0-9_]+"))throw new IllegalArgumentException("Invalid execution event sequence or stage");String occurred=DeliveryRules.required(e.get("created_at"),60);Instant.parse(occurred);clean.add(Map.of("sequence",sequence,"event",stage,"message",text(e.get("message"),6000),"occurredAt",occurred));}return clean;
 }
}
