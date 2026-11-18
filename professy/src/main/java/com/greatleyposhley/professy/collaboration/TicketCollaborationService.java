package com.greatleyposhley.professy.collaboration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;
import java.nio.file.*;
import java.sql.Statement;
import java.util.*;
import static com.greatleyposhley.professy.collaboration.WorkAccess.number;

@Service
public class TicketCollaborationService {
    private final WorkAccess access;
    private final ObjectMapper json;
    private final Path storage;
    private final TicketActors ticketActors;
    private final WorkspaceAgents agents;
    private final TicketAuditService audit;
    public TicketCollaborationService(WorkAccess access,ObjectMapper json,@Value("${app.attachments.directory:./.runtime/attachments}") String path,TicketActors ticketActors,WorkspaceAgents agents,TicketAuditService audit){this.access=access;this.json=json;this.storage=Path.of(path).toAbsolutePath().normalize();this.ticketActors=ticketActors;this.agents=agents;this.audit=audit;}
    public List<Map<String,Object>> activity(String kind,long id,long before){access.ticket(kind,id,false);return access.jdbc.queryForList("SELECT a.*,CONCAT_WS(' ',u.first_name,u.last_name) AS actorName FROM ticket_activity a LEFT JOIN user_account u ON u.id=a.actor_id WHERE a.org_id=? AND a.ticket_type=? AND a.ticket_id=? AND (?=0 OR a.id<?) ORDER BY a.id DESC LIMIT 50",access.actor().getOrgId(),access.kind(kind),id,before,before).stream().map(row->{row.put("changes",json.readValue(row.remove("changes_json").toString(),Map.class));var changes=(Map<?,?>)row.get("changes");Object after=changes.get("after");if(after instanceof Map<?,?> detail&&"AGENT".equals(detail.get("actorType"))){row.put("actorType","AGENT");row.put("actorName",detail.get("agentName"));}return row;}).toList();}
    public Map<String,Object> context(String type,long id){
        String kind=access.kind(type);var row=access.ticket(kind,id,false);var actor=access.actor();
        var comments=access.jdbc.queryForList("SELECT c.id,c.content,c.author_id AS authorId,c.created_at AS createdAt,c.updated_at AS updatedAt,CONCAT_WS(' ',u.first_name,u.last_name) AS authorName,u.profile_picture_url AS avatarUrl FROM ticket_comments c JOIN user_account u ON u.id=c.author_id WHERE c.org_id=? AND c.ticket_type=? AND c.ticket_id=? AND c.deleted_at IS NULL ORDER BY c.id",actor.getOrgId(),kind,id);
        comments.forEach(comment->comment.put("canEdit",actor.getId()==number(comment.get("authorId"))));
        var files=access.jdbc.queryForList("SELECT f.id,f.original_name AS name,f.content_type AS contentType,f.byte_size AS size,f.uploaded_by AS uploadedBy,f.created_at AS createdAt,CONCAT_WS(' ',u.first_name,u.last_name) AS uploadedByName FROM ticket_attachments f LEFT JOIN user_account u ON u.id=f.uploaded_by WHERE f.org_id=? AND f.ticket_type=? AND f.ticket_id=? AND f.deleted_at IS NULL ORDER BY f.id DESC",actor.getOrgId(),kind,id);
        files.forEach(file->{file.put("canDelete",access.admin(actor)||actor.getId()==number(file.get("uploadedBy")));var evidence=access.jdbc.queryForList("SELECT a.display_name AS agentName,e.job_id FROM delivery_evidence e JOIN workspace_actors a ON a.id=e.agent_id WHERE e.attachment_id=? AND e.org_id=?",file.get("id"),actor.getOrgId());if(!evidence.isEmpty()){file.put("uploadedByName",evidence.getFirst().get("agentName"));file.put("actorType","AGENT");file.put("runId",evidence.getFirst().get("job_id"));}});
        return Map.of("projectId",number(row.get("work_project_id")),"permissions",Map.of("canEdit",access.canEdit(actor,row),"canComment",true,"canAttach",true),"comments",comments,"attachments",files,"activity",activity(kind,id,0),"currentUserId",actor.getId(),"watching",access.jdbc.queryForObject("SELECT COUNT(*) FROM ticket_watchers WHERE org_id=? AND ticket_type=? AND ticket_id=? AND user_id=?",Integer.class,actor.getOrgId(),kind,id,actor.getId())>0);
    }
    @Transactional
    public void record(String kind,long id,String action,Object before,Object after){var row=access.ticket(kind,id,false);audit.recordManual(access.kind(kind),id,access.actor().getOrgId(),number(row.get("work_project_id")),access.actor().getId(),action,before,after,row);}
    @Transactional
    public long addComment(String kind,long id,String content){
        var row=access.ticket(kind,id,false);String text=content==null?"":content.trim();if(text.isEmpty()||text.length()>20000)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Write a comment of 1–20,000 characters");
        var key=new GeneratedKeyHolder();access.jdbc.update(connection->{var statement=connection.prepareStatement("INSERT INTO ticket_comments(org_id,project_id,ticket_type,ticket_id,author_id,content) VALUES (?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);statement.setLong(1,access.actor().getOrgId());statement.setLong(2,number(row.get("work_project_id")));statement.setString(3,access.kind(kind));statement.setLong(4,id);statement.setLong(5,access.actor().getId());statement.setString(6,text);return statement;},key);
        record(kind,id,"COMMENT_ADDED",Map.of(),Map.of("content",text));return Objects.requireNonNull(key.getKey()).longValue();
    }
    @Transactional
    public void changeComment(String kind,long id,long commentId,String content,boolean delete){
        access.ticket(kind,id,false);var rows=access.jdbc.queryForList("SELECT * FROM ticket_comments WHERE id=? AND org_id=? AND ticket_type=? AND ticket_id=? AND deleted_at IS NULL",commentId,access.actor().getOrgId(),access.kind(kind),id);
        if(rows.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Comment not found");var row=rows.getFirst();
        if(number(row.get("author_id"))!=access.actor().getId())throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the author can change this comment");
        String text=content==null?"":content.trim();if(!delete&&(text.isEmpty()||text.length()>20000))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Write a comment of 1–20,000 characters");
        if(delete)access.jdbc.update("UPDATE ticket_comments SET deleted_at=CURRENT_TIMESTAMP(6) WHERE id=?",commentId);else access.jdbc.update("UPDATE ticket_comments SET content=?,updated_at=CURRENT_TIMESTAMP(6) WHERE id=?",text,commentId);
        record(kind,id,delete?"COMMENT_DELETED":"COMMENT_UPDATED",Map.of("content",row.get("content")),delete?Map.of():Map.of("content",text));
    }
    @Transactional
    public void upload(String kind,long id,MultipartFile file)throws Exception{
        var row=access.ticket(kind,id,false);if(file.isEmpty()||file.getSize()>25*1024*1024)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose a file between 1 byte and 25 MB");
        String original=Path.of(Objects.requireNonNullElse(file.getOriginalFilename(),"attachment").replace('\\','/')).getFileName().toString().replaceAll("[\\p{Cntrl}]","");if(original.length()>180)original=original.substring(original.length()-180);
        String name=access.actor().getOrgId()+"/"+UUID.randomUUID();Path target=storage.resolve(name).normalize();Files.createDirectories(target.getParent());try(var input=file.getInputStream()){Files.copy(input,target);}catch(Exception failure){Files.deleteIfExists(target);throw failure;}
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){@Override public void afterCompletion(int status){if(status!=STATUS_COMMITTED)try{Files.deleteIfExists(target);}catch(Exception ignored){}}});
        access.jdbc.update("INSERT INTO ticket_attachments(org_id,project_id,ticket_type,ticket_id,uploaded_by,original_name,storage_name,content_type,byte_size) VALUES (?,?,?,?,?,?,?,?,?)",access.actor().getOrgId(),number(row.get("work_project_id")),access.kind(kind),id,access.actor().getId(),original,name,Objects.requireNonNullElse(file.getContentType(),"application/octet-stream"),file.getSize());
        record(kind,id,"ATTACHMENT_ADDED",Map.of(),Map.of("name",original,"size",file.getSize()));
    }
    public Map<String,Object> file(String kind,long id,long attachmentId){access.ticket(kind,id,false);var rows=access.jdbc.queryForList("SELECT * FROM ticket_attachments WHERE id=? AND org_id=? AND ticket_type=? AND ticket_id=? AND deleted_at IS NULL",attachmentId,access.actor().getOrgId(),access.kind(kind),id);if(rows.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Attachment not found");return rows.getFirst();}
    public Path filePath(Map<String,Object> file){Path path=storage.resolve(file.get("storage_name").toString()).normalize();if(!path.startsWith(storage))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Attachment not found");return path;}
    @Transactional
    public void deleteFile(String kind,long id,long attachmentId){var file=file(kind,id,attachmentId);if(!access.admin(access.actor())&&number(file.get("uploaded_by"))!=access.actor().getId())throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the uploader can remove this attachment");access.jdbc.update("UPDATE ticket_attachments SET deleted_at=CURRENT_TIMESTAMP(6) WHERE id=?",attachmentId);record(kind,id,"ATTACHMENT_DELETED",Map.of("name",file.get("original_name")),Map.of());}
    @Transactional public void place(String type,long id,Map<String,Object> body){
        var kind=access.kind(type);var row=access.ticket(kind,id,false);access.require("TICKETS.MOVE");
        if(!body.containsKey("status")&&!body.containsKey("sprintId"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose a status or sprint");
        String table=sprintWorkTable(kind);var before=new LinkedHashMap<String,Object>();before.put("title",Objects.toString(row.get("title"),Objects.toString(row.get("name"),kind+"-"+id)));before.put("status",row.get("status"));before.put("sprintId",row.get("sprint_id"));
        placeWork(table, id, access.actor().getOrgId(), row, body);
        var updated=access.jdbc.queryForMap("SELECT status,sprint_id FROM "+table+" WHERE id=? AND org_id=?",id,access.actor().getOrgId());var after=new LinkedHashMap<String,Object>();after.put("title",before.get("title"));after.put("status",updated.get("status"));after.put("sprintId",updated.get("sprint_id"));
        if(!before.equals(after))record(kind,id,"UPDATED",before,after);
    }

    @Transactional public void assignUnassigned(String type, long id, Map<String,Object> body) {
        String kind = access.kind(type);
        String table = sprintWorkTable(kind);
        var ticket = access.ticket(kind, id, true);
        long orgId = access.actor().getOrgId();
        long projectId = number(ticket.get("work_project_id"));
        long assigneeId = number(body.get("assigneeId"));
        if (assigneeId == 0 || assigneeId == Long.MIN_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose an assignee");
        }
        var rows = access.jdbc.queryForList(
            "SELECT status,sprint_id,assignee_user_id FROM " + table + " WHERE id=? AND org_id=? FOR UPDATE", id, orgId);
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        var row = rows.getFirst();
        if (row.get("assignee_user_id") != null || !access.jdbc.queryForList(
            "SELECT actor_id FROM ticket_actor_assignments WHERE org_id=? AND ticket_type=? AND ticket_id=? AND assignment_role='ASSIGNEE' FOR UPDATE",
            Long.class, orgId, kind, id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This item was already assigned");
        }

        if (assigneeId > 0) {
            if (access.jdbc.queryForObject("SELECT COUNT(*) FROM user_account WHERE id=? AND org_id=? AND is_active=1",
                Integer.class, assigneeId, orgId) == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose an active user in this organization");
            }
        } else if (!"ACTIVE".equals(agents.row(-assigneeId).get("status")) || !agents.projects(-assigneeId).contains(projectId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose an active agent assigned to this project");
        }

        if (body.containsKey("sprintId")) {
            access.require("TICKETS.MOVE");
            if (number(body.get("sprintId")) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a sprint in this project");
            }
            if (number(body.get("sprintId")) != number(row.get("sprint_id"))) {
                row.put("work_project_id", projectId);
                placeWork(table, id, orgId, row, Map.of("sprintId", body.get("sprintId")));
            }
        }
        ticketActors.finish(List.of(new TicketActors.Change(kind, id, Map.of("assignee", assigneeId))), new byte[0]);
    }

    private String sprintWorkTable(String kind) {
        return switch (kind) {
            case "EPIC" -> "epics";
            case "STORY" -> "stories";
            case "TASK" -> "tasks";
            case "ISSUE" -> "issues";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test cases are not sprint work items");
        };
    }

    private void placeWork(String table, long id, long orgId, Map<String,Object> row, Map<String,Object> body) {
        String status = Objects.toString(row.get("status"), "BACKLOG");
        if (body.containsKey("status")) status = access.policy.validateStatus(orgId, body.get("status"));
        Object sprint = row.get("sprint_id");
        if (body.containsKey("sprintId")) {
            long target = number(body.get("sprintId"));
            if (target > 0 && access.jdbc.queryForObject("SELECT COUNT(*) FROM sprints WHERE id=? AND org_id=? AND project_id=?",
                Integer.class, target, orgId, number(row.get("work_project_id"))) == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a sprint in this project");
            }
            sprint = target > 0 ? target : null;
            if (target == 0) status = "BACKLOG";
            else if (status.equals("BACKLOG")) status = "TODO";
        }
        access.jdbc.update("UPDATE " + table + " SET status=?,sprint_id=? WHERE id=? AND org_id=?", status, sprint, id, orgId);
    }
    @Transactional
    public void watch(String kind,long id,boolean enabled){var row=access.ticket(kind,id,false);if(enabled)access.jdbc.update("INSERT IGNORE INTO ticket_watchers(org_id,project_id,ticket_type,ticket_id,user_id) VALUES(?,?,?,?,?)",access.actor().getOrgId(),number(row.get("work_project_id")),access.kind(kind),id,access.actor().getId());else access.jdbc.update("DELETE FROM ticket_watchers WHERE org_id=? AND ticket_type=? AND ticket_id=? AND user_id=?",access.actor().getOrgId(),access.kind(kind),id,access.actor().getId());}
}
