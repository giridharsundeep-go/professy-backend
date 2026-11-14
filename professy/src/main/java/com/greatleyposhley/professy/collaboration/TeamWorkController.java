package com.greatleyposhley.professy.collaboration;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;

@RestController
public class TeamWorkController {
    private final WorkAccess access;
    public TeamWorkController(WorkAccess access){this.access=access;}
    @GetMapping("/api/teams/{id}/work") public Object work(@PathVariable long id,@RequestParam(required=false) Long projectId){
        var user=access.actor();if(access.jdbc.queryForObject("SELECT COUNT(*) FROM teams WHERE id=? AND org_id=?",Integer.class,id,user.getOrgId())==0)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Team not found");
        Set<Long> projects=access.projects(user);if(projectId!=null){access.project(projectId);projects.retainAll(Set.of(projectId));}if(projects.isEmpty())return List.of();
        String scope=String.join(",",Collections.nCopies(projects.size(),"?"));List<Map<String,Object>> results=new ArrayList<>();
        for(String kind:List.of("EPIC","STORY","TASK","ISSUE")){
            String table=switch(kind){case "EPIC"->"epics";case "STORY"->"stories";case "TASK"->"tasks";default->"issues";};
            String project=kind.equals("TASK")?"parent.project_id":"t.project_id";
            String join=kind.equals("TASK")?" JOIN stories parent ON parent.id=t.story_id":"";
            String title=kind.equals("EPIC")?"t.name":"t.title";
            List<Object> args=new ArrayList<>(List.of(user.getOrgId(),id));args.addAll(projects);
            var rows=access.jdbc.queryForList("SELECT t.id,"+project+" AS projectId,"+title+" AS title,t.status,t.sprint_id AS sprintId,t.assignee_user_id AS assigneeId,CONCAT_WS(' ',u.first_name,u.last_name) AS assigneeName,s.name AS sprintName FROM "+table+" t"+join+" LEFT JOIN user_account u ON u.id=t.assignee_user_id LEFT JOIN sprints s ON s.id=t.sprint_id WHERE t.org_id=? AND EXISTS(SELECT 1 FROM team_members tm WHERE tm.team_id=? AND tm.user_id=t.assignee_user_id) AND "+project+" IN ("+scope+") ORDER BY t.updated_at DESC",args.toArray());
            for(var row:rows){row.put("kind",kind);row.put("key",kind+"-"+row.get("id"));results.add(row);}
        }
        return results;
    }
}
