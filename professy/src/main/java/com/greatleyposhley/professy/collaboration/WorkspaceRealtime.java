package com.greatleyposhley.professy.collaboration;
import com.greatleyposhley.professy.entities.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.context.annotation.Bean;
import org.apache.kafka.clients.admin.NewTopic;
import tools.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.concurrent.*;

/** Kafka fan-out across instances; each browser stream is scoped to its authenticated organization. */
@Service
public class WorkspaceRealtime {
 private final JdbcTemplate jdbc;private final KafkaTemplate<String,String> kafka;private final ObjectMapper json;private final TransactionTemplate tx;
 private final ConcurrentMap<Long,CopyOnWriteArrayList<Client>> clients=new ConcurrentHashMap<>();
 public WorkspaceRealtime(JdbcTemplate jdbc,KafkaTemplate<String,String> kafka,ObjectMapper json,PlatformTransactionManager manager){this.jdbc=jdbc;this.kafka=kafka;this.json=json;this.tx=new TransactionTemplate(manager);}
 @Bean NewTopic workspaceSettingsTopic(){return new NewTopic("professy.workspace-events.v1",3,(short)1);}
 private record Client(long user,SseEmitter emitter){}
 public SseEmitter subscribe(UserAccount user){var emitter=new SseEmitter(120000L);var list=clients.computeIfAbsent(user.getOrgId(),key->new CopyOnWriteArrayList<>());var client=new Client(user.getId(),emitter);list.add(client);Runnable remove=()->{list.remove(client);if(list.isEmpty())clients.remove(user.getOrgId(),list);};emitter.onCompletion(remove);emitter.onTimeout(()->{remove.run();emitter.complete();});emitter.onError(error->remove.run());try{emitter.send(SseEmitter.event().name("ready").data("{}"));}catch(Exception error){remove.run();emitter.complete();}return emitter;}
 @Scheduled(fixedDelay=1000)public void publish(){tx.executeWithoutResult(status->{for(var row:jdbc.queryForList("SELECT * FROM workspace_event_outbox WHERE published_at IS NULL ORDER BY id LIMIT 25 FOR UPDATE SKIP LOCKED")){try{var event=Map.of("eventId",row.get("event_id"),"orgId",row.get("org_id"),"type",row.get("event_type"),"userId",row.get("user_id"));kafka.send("professy.workspace-events.v1",row.get("org_id").toString(),json.writeValueAsString(event)).get(5,TimeUnit.SECONDS);jdbc.update("UPDATE workspace_event_outbox SET published_at=CURRENT_TIMESTAMP(6) WHERE id=?",row.get("id"));}catch(Exception error){if(error instanceof InterruptedException)Thread.currentThread().interrupt();break;}}});}
 @KafkaListener(topics={"professy.workspace-events.v1","${app.events.topic:professy.ticket-events.v1}"},groupId="professy-workspace-ui-${random.uuid}")
 public void receive(String value){var event=json.readValue(value,Map.class);long org=WorkAccess.number(event.get("orgId"));var list=clients.get(org);if(list==null)return;var payload=new LinkedHashMap<String,Object>();for(String key:List.of("eventId","type","userId","ticketType","ticketId","projectId"))if(event.get(key)!=null)payload.put(key,event.get(key));for(var client:list)try{client.emitter().send(SseEmitter.event().name("workspace").data(json.writeValueAsString(payload)));}catch(Exception error){list.remove(client);client.emitter().complete();}}
 /** Private app events expose only identifiers, and only to the affected people. */
 public static boolean recipient(Map<?,?> event,long user){return WorkAccess.number(event.get("actorId"))==user||WorkAccess.number(event.get("ownerId"))==user||(event.get("recipients") instanceof List<?> people&&people.stream().anyMatch(id->WorkAccess.number(id)==user));}
 @KafkaListener(topics={"greatley.calendar.events.v1","chatly.events.v1"},groupId="professy-private-ui-${random.uuid}")
 public void receivePrivate(String value){var event=json.readValue(value,Map.class);var list=clients.get(WorkAccess.number(event.get("orgId")));if(list==null)return;boolean calendar=event.containsKey("calendarEventId");var payload=new LinkedHashMap<String,Object>();payload.put("type",calendar?"CALENDAR_UPDATED":"CHAT_UPDATED");for(String key:List.of("eventId","calendarEventId","conversationId"))if(event.get(key)!=null)payload.put(key,event.get(key));for(var client:list){if(!recipient(event,client.user()))continue;try{client.emitter().send(SseEmitter.event().name("workspace").data(json.writeValueAsString(payload)));}catch(Exception error){list.remove(client);client.emitter().complete();}}}
 @Scheduled(fixedDelay=25000)public void heartbeat(){for(var list:clients.values())for(var client:list)try{client.emitter().send(SseEmitter.event().comment("keep-alive"));}catch(Exception error){list.remove(client);client.emitter().complete();}}
}
