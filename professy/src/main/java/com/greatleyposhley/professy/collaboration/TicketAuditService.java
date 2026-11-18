package com.greatleyposhley.professy.collaboration;

import com.greatleyposhley.professy.entities.Epics;
import com.greatleyposhley.professy.entities.Issues;
import com.greatleyposhley.professy.entities.Stories;
import com.greatleyposhley.professy.entities.Tasks;
import com.greatleyposhley.professy.entities.Testcases;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Writes ticket activity and its transactional Kafka outbox entry from application code. */
@Service
public class TicketAuditService {
    private final JdbcTemplate jdbc;
    private final ObjectMapper json;

    public TicketAuditService(JdbcTemplate jdbc, ObjectMapper json) {
        this.jdbc = jdbc;
        this.json = json;
    }

    public Map<String, Object> snapshot(Object entity) {
        var result = new LinkedHashMap<String, Object>();
        if (entity instanceof Epics value) {
            result.put("title", value.getName()); result.put("description", value.getDescription());
            result.put("status", value.getStatus()); result.put("assigneeId", id(value.getAssignee()));
            result.put("reporterId", id(value.getReporter())); result.put("creatorId", id(value.getCreator()));
            result.put("sprintId", id(value.getSprint())); result.put("key", value.getEpicCode());
        } else if (entity instanceof Stories value) {
            result.put("title", value.getTitle()); result.put("description", value.getDescription());
            result.put("status", value.getStatus()); result.put("priority", value.getPriority());
            result.put("points", value.getStoryPoints()); result.put("assigneeId", id(value.getAssignee()));
            result.put("reporterId", id(value.getReporter())); result.put("creatorId", id(value.getCreator()));
            result.put("sprintId", id(value.getSprint())); result.put("epicId", id(value.getEpic()));
        } else if (entity instanceof Tasks value) {
            result.put("title", value.getTitle()); result.put("description", value.getDescription());
            result.put("status", value.getStatus()); result.put("assigneeId", id(value.getAssignee()));
            result.put("reporterId", id(value.getReporter())); result.put("creatorId", id(value.getCreator()));
            result.put("sprintId", id(value.getSprint())); result.put("storyId", id(value.getStory()));
        } else if (entity instanceof Issues value) {
            result.put("title", value.getTitle()); result.put("description", value.getDescription());
            result.put("status", value.getStatus()); result.put("assigneeId", id(value.getAssignee()));
            result.put("reporterId", id(value.getReporter())); result.put("creatorId", id(value.getCreator()));
            result.put("sprintId", id(value.getSprint())); result.put("key", value.getIssueCode());
        } else if (entity instanceof Testcases value) {
            result.put("title", value.getTitle()); result.put("description", value.getDescription());
            result.put("status", value.getStatus() == null ? null : value.getStatus().name());
            result.put("priority", value.getPriority() == null ? null : value.getPriority().name());
            result.put("steps", value.getSteps()); result.put("preconditions", value.getPreconditions());
            result.put("expectedResult", value.getExpectedResult()); result.put("actualResult", value.getActualResult());
            result.put("type", value.getType()); result.put("assigneeId", id(value.getUser()));
            result.put("creatorId", id(value.getCreator())); result.put("epicId", id(value.getEpic()));
            result.put("storyId", id(value.getStory())); result.put("taskId", id(value.getTask()));
            result.put("issueId", id(value.getIssue()));
        }
        return result;
    }

    public void recordTags(Testcases testcase, Set<String> before, Set<String> after) {
        Context context = context(testcase);
        if (context == null) return;
        Long actor = jdbc.queryForObject("SELECT COALESCE(@professy_actor_id,0)", Long.class);
        long actorId = actor == null ? 0 : actor;
        for (String tag : after) if (!before.contains(tag)) record(context, actorId, "TAG_ADDED", Map.of(), Map.of("tag", tag), Map.of());
        for (String tag : before) if (!after.contains(tag)) record(context, actorId, "TAG_REMOVED", Map.of("tag", tag), Map.of(), Map.of());
    }

    public void recordEntity(Object entity, String action, Map<String, Object> before, Map<String, Object> after) {
        Context context = context(entity);
        if (context == null) return;
        Long actor = jdbc.queryForObject("SELECT COALESCE(@professy_actor_id,0)", Long.class);
        record(context, actor == null ? 0 : actor, action, before, after, Map.of());
    }

    public void recordManual(String type, long ticketId, long orgId, long projectId, long actorId,
                             String action, Object before, Object after, Map<String, Object> ticketRow) {
        if (projectId <= 0) return;
        var context = new Context(type, ticketId, orgId, projectId, Map.of());
        record(context, actorId, action, asMap(before), asMap(after), ticketRow == null ? Map.of() : ticketRow);
    }

    private void record(Context context, long actorId, String action, Map<String, Object> before,
                        Map<String, Object> after, Map<String, Object> row) {
        if (context.projectId() <= 0) return;
        Map<String, Object> beforeValue = before == null ? Map.of() : before;
        Map<String, Object> afterValue = after == null ? Map.of() : after;
        String eventId = UUID.randomUUID().toString();
        String title = text(afterValue.get("title"), text(beforeValue.get("title"), context.type() + "-" + context.id()));
        String summary = (action.replace('_', ' ') + ": " + title);
        if (summary.length() > 250) summary = summary.substring(0, 250);
        var changes = new LinkedHashMap<String, Object>();
        changes.put("before", beforeValue); changes.put("after", afterValue);
        jdbc.update("INSERT INTO ticket_activity(event_id,org_id,project_id,ticket_type,ticket_id,actor_id,event_type,summary,changes_json) VALUES(?,?,?,?,?,?,?,?,?)",
                eventId, context.orgId(), context.projectId(), context.type(), context.id(), Math.max(0, actorId), action, summary, json.writeValueAsString(changes));
        jdbc.update("INSERT INTO ticket_event_outbox(event_id,aggregate_key,payload) VALUES(?,?,?)",
                eventId, context.orgId() + ":" + context.type() + ":" + context.id(), json.writeValueAsString(Map.of("eventId", eventId)));
        addWatchers(context, actorId, beforeValue, afterValue, row);
    }

    private void addWatchers(Context context, long actorId, Map<String, Object> before,
                             Map<String, Object> after, Map<String, Object> row) {
        Set<Long> people = new LinkedHashSet<>();
        add(people, actorId);
        for (Map<String, Object> values : List.of(before, after, row)) {
            add(people, number(values.get("assigneeId"))); add(people, number(values.get("assignee_user_id")));
            add(people, number(values.get("reporterId"))); add(people, number(values.get("reporter_user_id")));
            add(people, number(values.get("creatorId"))); add(people, number(values.get("creator_user_id")));
            add(people, number(values.get("created_by"))); add(people, number(values.get("user_id")));
        }
        if (people.isEmpty()) return;
        String slots = String.join(",", java.util.Collections.nCopies(people.size(), "?"));
        var args = new ArrayList<Object>();
        args.add(context.orgId()); args.add(context.projectId()); args.add(context.type()); args.add(context.id());
        args.add(context.orgId()); args.addAll(people);
        jdbc.update("INSERT IGNORE INTO ticket_watchers(org_id,project_id,ticket_type,ticket_id,user_id) " +
                "SELECT ?,?,?,?,u.id FROM user_account u WHERE u.org_id=? AND u.id IN (" + slots + ")", args.toArray());
    }

    private Context context(Object entity) {
        if (entity instanceof Epics value) return new Context("EPIC", value.getId(), value.getOrgId(), id(value.getProject()), snapshot(entity));
        if (entity instanceof Stories value) return new Context("STORY", value.getId(), value.getOrgId(), id(value.getProject()), snapshot(entity));
        if (entity instanceof Tasks value) {
            var story = value.getStory();
            return new Context("TASK", value.getId(), value.getOrgId(), story == null ? 0 : id(story.getProject()), snapshot(entity));
        }
        if (entity instanceof Issues value) return new Context("ISSUE", value.getId(), value.getOrgId(), id(value.getProject()), snapshot(entity));
        if (entity instanceof Testcases value) return new Context("TEST_CASE", value.getId(), value.getOrgId(), id(value.getProject()), snapshot(entity));
        return null;
    }

    private static Long id(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.longValue();
        if (value instanceof com.greatleyposhley.professy.entities.UserAccount user) return user.getId();
        if (value instanceof com.greatleyposhley.professy.entities.Projects project) return project.getId();
        if (value instanceof com.greatleyposhley.professy.entities.Sprints sprint) return sprint.getId();
        if (value instanceof Epics epic) return epic.getId();
        if (value instanceof Stories story) return story.getId();
        if (value instanceof Tasks task) return task.getId();
        if (value instanceof Issues issue) return issue.getId();
        if (value instanceof Testcases testcase) return testcase.getId();
        if (value instanceof Map<?, ?> map) return id(map.get("id"));
        return null;
    }

    private static long number(Object value) {
        if (value == null) return 0;
        try { return Long.parseLong(value.toString()); } catch (NumberFormatException ignored) { return 0; }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value) {
        return value instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }

    private static void add(Set<Long> values, long value) { if (value > 0) values.add(value); }
    private static void add(Set<Long> values, Long value) { if (value != null && value > 0) values.add(value); }
    private static String text(Object value, String fallback) { return value == null ? fallback : value.toString(); }
    private record Context(String type, Long id, Long orgId, long projectId, Map<String, Object> values) { }
}
