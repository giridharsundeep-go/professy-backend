package com.greatleyposhley.professy.collaboration;

import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;

@Service
public class WorkAccess {
    private final UserAccountRepository users;
    public final JdbcTemplate jdbc;public final WorkspacePolicy policy;
    public WorkAccess(UserAccountRepository users, JdbcTemplate jdbc,WorkspacePolicy policy) { this.users=users;this.jdbc=jdbc;this.policy=policy; }
    public UserAccount actor() {
        var authentication=SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !authentication.isAuthenticated()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Sign in to continue");
        return users.findByEmail(authentication.getName()).filter(user->Boolean.TRUE.equals(user.getIsActive()))
            .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account is unavailable"));
    }
    public boolean admin(UserAccount user) { return user.getOrgRole()==OrgRole.SUPER_USER; }
    public void requireAdmin() { if(!admin(actor())) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Organization administrator access is required"); }
    public void require(String permission){policy.require(actor(),permission);}
    public Set<Long> projects(UserAccount user) {return new HashSet<>(jdbc.queryForList("SELECT id FROM projects WHERE org_id=?",Long.class,user.getOrgId()));}
    public void project(long id) { if(!projects(actor()).contains(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Project is not available in your organization"); }
    public String kind(String value) {
        return switch(value.toUpperCase(Locale.ROOT).replace('-','_')) { case "EPIC","EPICS"->"EPIC";case "STORY","STORIES"->"STORY";case "TASK","TASKS"->"TASK";case "ISSUE","ISSUES"->"ISSUE";case "TEST_CASE","TESTCASES"->"TEST_CASE";default->throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unsupported ticket type"); };
    }
    public Map<String,Object> row(String value,long id) {
        String kind=kind(value);
        String table=switch(kind){case "EPIC"->"epics";case "STORY"->"stories";case "TASK"->"tasks";case "ISSUE"->"issues";default->"testcases";};
        String project=kind.equals("TASK")?"(SELECT project_id FROM stories WHERE id=t.story_id)":"t.project_id";
        var rows=jdbc.queryForList("SELECT t.*, "+project+" AS work_project_id FROM "+table+" t WHERE t.id=? AND t.org_id=?",id,actor().getOrgId());
        if(rows.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Ticket not found");
        return rows.getFirst();
    }
    public Map<String,Object> ticket(String kind,long id,boolean edit) {
        require(kind(kind).equals("TEST_CASE")?"TESTCASES.READ":"TICKETS.READ");var row=row(kind,id);project(number(row.get("work_project_id")));
        var user=actor();
        if(edit&&!canEdit(user,row))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You do not have permission to edit this ticket");
        return row;
    }
    public boolean canEdit(UserAccount user,Map<String,Object> row) {
        return policy.can(user,row.containsKey("test_case_code")?"TESTCASES.UPDATE":"TICKETS.UPDATE");
    }
    public void executionRequester(long id){var user=users.findById(id).filter(u->Boolean.TRUE.equals(u.getIsActive())).orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN,"Requesting user is no longer active"));policy.require(user,"TICKETS.READ");policy.require(user,"TICKETS.UPDATE");}
    public static long number(Object value){if(value==null)return 0;try{return Long.parseLong(value.toString());}catch(NumberFormatException ignored){return 0;}}
}
