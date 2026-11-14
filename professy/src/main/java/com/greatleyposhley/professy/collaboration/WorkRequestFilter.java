package com.greatleyposhley.professy.collaboration;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.persistence.EntityManager;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/** One transaction keeps the authenticated actor and database-trigger events with every write. */
public class WorkRequestFilter extends OncePerRequestFilter {
    private final TicketActors actors;private final WorkAccess access;private final TransactionTemplate transactions;private final EntityManager entityManager;private final ObjectMapper json;
    public WorkRequestFilter(WorkAccess access,PlatformTransactionManager manager,EntityManager entityManager,ObjectMapper json,TicketActors actors){this.actors=actors;this.access=access;this.transactions=new TransactionTemplate(manager);this.entityManager=entityManager;this.json=json;}
    @Override protected boolean shouldNotFilter(HttpServletRequest r){return r.getRequestURI().startsWith("/api/delivery-worker/")||r.getRequestURI().startsWith("/api/meeting-runtime/")||r.getRequestURI().startsWith("/api/notifications")||r.getRequestURI().startsWith("/api/internal/")||!r.getRequestURI().startsWith("/api/")||Set.of("/api/auth/login","/api/auth/handoff/consume","/api/auth/register","/api/auth/organisation/validate","/api/auth/password/request","/api/auth/password/reset","/api/auth/email/verify").contains(r.getRequestURI())||r.getMethod().equals("OPTIONS");}
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws IOException,ServletException{
        boolean write=!Set.of("GET","HEAD","OPTIONS").contains(request.getMethod());
        try{
            HttpServletRequest input=request;
            Object body=null;
            if(write&&Optional.ofNullable(request.getContentType()).orElse("").contains("application/json")){
                byte[] bytes=request.getInputStream().readNBytes(4*1024*1024+1);
                if(bytes.length>4*1024*1024)throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,"Request is too large");
                input=new RepeatableRequest(request,bytes);if(bytes.length>0)body=json.readValue(bytes,Object.class);
            }
            var actorChanges=write?actors.prepare(request.getRequestURI(),body):java.util.List.<TicketActors.Change>of();
            authorize(input,write,body);
            if(body!=null)input=new RepeatableRequest(request,json.writeValueAsString(body).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            if(!write){chain.doFilter(input,response);return;}
            var buffered=new ContentCachingResponseWrapper(response);final HttpServletRequest ready=input;
            transactions.executeWithoutResult(status->{
                access.jdbc.update("SET @professy_actor_id=?",access.actor().getId());
                try{chain.doFilter(ready,buffered);if(buffered.getStatus()>=400)status.setRollbackOnly();else {entityManager.flush();actors.finish(actorChanges,buffered.getContentAsByteArray());if(ready.getMethod().equals("DELETE"))actors.deleted(ready.getRequestURI());}}
                catch(IOException|ServletException error){throw new RequestFailure(error);}
                finally{access.jdbc.update("SET @professy_actor_id=NULL");}
            });
            buffered.copyBodyToResponse();
        }catch(ResponseStatusException error){response.resetBuffer();response.setStatus(error.getStatusCode().value());response.setContentType("application/json");response.getWriter().write(json.writeValueAsString(Map.of("message",Optional.ofNullable(error.getReason()).orElse("Request denied"))));}
        catch(IllegalArgumentException error){response.resetBuffer();response.setStatus(400);response.setContentType("application/json");response.getWriter().write(json.writeValueAsString(Map.of("message",Objects.toString(error.getMessage(),"Invalid request"))));}
        catch(RequestFailure failure){if(failure.getCause() instanceof IOException e)throw e;throw (ServletException)failure.getCause();}
    }
    private void authorize(HttpServletRequest r,boolean write,Object body){
        var user=access.actor();String path=r.getRequestURI().replaceFirst("^/api/(v1/)?","");
        if(path.startsWith("auth/")){access.requireAdmin();if(r.getParameter("orgId")!=null&&WorkAccess.number(r.getParameter("orgId"))!=user.getOrgId())throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Organization access denied");return;}
        if(path.startsWith("retrospectives")||path.startsWith("incident-tickets")||path.startsWith("operations")||path.startsWith("board-filters")||path.startsWith("workspace/agents")||path.startsWith("workspace/files")||path.startsWith("workspace/meetings")||path.startsWith("workspace/settings")||path.startsWith("workspace/documents")||path.startsWith("workspace/organization")||path.startsWith("workspace/roadmap")||path.startsWith("tickets/")||path.startsWith("notifications")||(path.equals("session/access")||path.equals("session/handoff")))return; // These controllers enforce their own resource permissions.
        if(WorkAccess.number(r.getParameter("projectId"))>0)access.project(WorkAccess.number(r.getParameter("projectId")));
        Matcher projects=Pattern.compile("(?:^|/)projects/(\\d+)(?:/|$)").matcher(path);if(projects.find())access.project(Long.parseLong(projects.group(1)));
        Matcher tickets=Pattern.compile("^(epics|stories|tasks|issues|testcases)/(\\d+)(?:/|$)").matcher(path);
        if(tickets.find())access.ticket(tickets.group(1),Long.parseLong(tickets.group(2)),false);
        Matcher relation=Pattern.compile("^(sprints|releases)/(\\d+)(?:/|$)").matcher(path);
        if(relation.find()){
            String table=relation.group(1);var ids=access.jdbc.queryForList("SELECT project_id FROM "+table+" WHERE id=? AND org_id=?",Long.class,Long.parseLong(relation.group(2)),user.getOrgId());
            if(ids.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found");access.project(ids.getFirst());
        }
        String resource=path.split("/")[0];
        resource=switch(resource){case "epics","stories","tasks","issues"->"TICKETS";case "roles"->"DESIGNATIONS";case "testcases","test-plans","test-suites"->"TESTCASES";case "project-teams","project-individual-members"->"PROJECTS";case "docs"->"DOCUMENTS";default->resource.toUpperCase(Locale.ROOT);};
        boolean ownProfile=(path.equals("users/"+user.getId())&&r.getMethod().equals("PUT"))||path.equals("user/upload-avatar");
        String action=!write?"READ":r.getMethod().equals("POST")?"CREATE":r.getMethod().equals("DELETE")?"DELETE":"UPDATE";
        if(write&&Set.of("RELEASES","PROJECTS","TEAMS","SPRINTS","BOARDS").contains(resource)&&path.matches("^[^/]+/[0-9]+/.+"))action="UPDATE";
        if(resource.equals("USERS")&&path.matches("^users/[0-9]+/deactivate$"))action="DELETE";
        if(WorkspacePolicy.RESOURCES.contains(resource)&&!ownProfile)access.require(resource+"."+action);
        else if(write&&!ownProfile)access.requireAdmin();
        if(!write)return;
        if(resource.equals("BOARDS")&&body instanceof Map<?,?> board){long project=WorkAccess.number(board.get("projectId"));if(project>0)access.project(project);}
        boolean ticketWrite=path.matches("^(epics|stories|tasks|issues|testcases)(/.*)?$");
        if(ticketWrite){
            if(body instanceof List<?> list){for(Object item:list)validateBody(item,!path.startsWith("testcases"));}
            else validateBody(body,!path.startsWith("testcases"));
        }
    }
    private void validateBody(Object body,boolean workflow){
        if(!(body instanceof Map<?,?> map))return;
        if(workflow&&map.get("status")!=null){
            String status=access.policy.validateStatus(access.actor().getOrgId(),map.get("status"));((Map<Object,Object>)map).put("status",status);
        }
        for(String field:List.of("projectId","project_id")){long id=WorkAccess.number(map.get(field));if(id>0)access.project(id);}
        if(map.get("project") instanceof Map<?,?> project)access.project(WorkAccess.number(project.get("id")));
        for(String kind:List.of("epic","story","task","issue")){
            Object value=map.get(kind);long id=value instanceof Map<?,?> v?WorkAccess.number(v.get("id")):WorkAccess.number(map.get(kind+"Id"));
            if(id==0)id=WorkAccess.number(map.get(kind+"_id"));if(id>0)access.ticket(kind,id,false);
        }
        for(String field:List.of("user","creator","assignee","reporter")){
            if(map.get(field) instanceof Map<?,?> person){long id=WorkAccess.number(person.get("id"));if(id>0&&access.jdbc.queryForObject("SELECT COUNT(*) FROM user_account WHERE id=? AND org_id=? AND is_active=1",Integer.class,id,access.actor().getOrgId())==0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Select an active user in your organization");}
        }
        for(String field:List.of("assigneeUserId","reporterUserId","assignee_user_id","reporter_user_id")){
            long id=WorkAccess.number(map.get(field));if(id>0&&access.jdbc.queryForObject("SELECT COUNT(*) FROM user_account WHERE id=? AND org_id=? AND is_active=1",Integer.class,id,access.actor().getOrgId())==0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Select an active user in your organization");
        }
    }
    private static class RequestFailure extends RuntimeException{RequestFailure(Exception e){super(e);}}
    private static class RepeatableRequest extends HttpServletRequestWrapper{
        private final byte[] bytes;RepeatableRequest(HttpServletRequest r,byte[] bytes){super(r);this.bytes=bytes;}
        @Override public ServletInputStream getInputStream(){var input=new ByteArrayInputStream(bytes);return new ServletInputStream(){public int read(){return input.read();}public boolean isFinished(){return input.available()==0;}public boolean isReady(){return true;}public void setReadListener(ReadListener listener){throw new UnsupportedOperationException();}};}
        @Override public BufferedReader getReader(){return new BufferedReader(new InputStreamReader(getInputStream(),java.nio.charset.StandardCharsets.UTF_8));}
    }
}
