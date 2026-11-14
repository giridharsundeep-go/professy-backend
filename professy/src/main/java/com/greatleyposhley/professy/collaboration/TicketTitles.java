package com.greatleyposhley.professy.collaboration;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;
import java.util.*;
@Service
public class TicketTitles {
 private final JdbcTemplate jdbc;private final ObjectMapper json;
 public TicketTitles(JdbcTemplate jdbc,ObjectMapper json){this.jdbc=jdbc;this.json=json;}
 public String title(String kind,long id,long org,Object changes){
  String table=switch(kind){case "EPIC"->"epics";case "STORY"->"stories";case "TASK"->"tasks";case "ISSUE"->"issues";case "TEST_CASE"->"testcases";default->null;};
  if(table==null)return "Conversation";
  var rows=jdbc.queryForList("SELECT "+(kind.equals("EPIC")?"name":"title")+" FROM "+table+" WHERE id=? AND org_id=?",String.class,id,org);if(!rows.isEmpty())return clean(rows.getFirst());
  if(changes!=null){var node=json.readTree(changes.toString());String value=node.path("after").path("title").asText(node.path("before").path("title").asText(""));if(!value.isBlank())return clean(value);}
  return "Work item no longer available";
 }
 public static String clean(String value){if(value==null||value.isBlank())return "Untitled work item";return value.replaceAll("\\s*\\[[A-Z][A-Z0-9_-]*-\\d[A-Z0-9_-]*\\]$","").trim();}
}
