package com.greatleyposhley.professy.collaboration;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;
/** Service-authenticated contracts; browser accounts cannot retrieve password links or recipients. */
@RestController @RequestMapping("/api/internal")
public class InternalEventController {
 private final JdbcTemplate jdbc;private final PasswordSetupService passwords;private final TicketEventPayload events;
 public InternalEventController(JdbcTemplate jdbc,PasswordSetupService passwords,TicketEventPayload events){this.jdbc=jdbc;this.passwords=passwords;this.events=events;}
 @GetMapping("/events/{eventId}") public Object event(@PathVariable String eventId){
  return events.resolve(eventId);
 }
 @GetMapping("/mail/{eventId}") @Transactional public Object mail(@PathVariable String eventId){
  var rows=jdbc.queryForList("SELECT q.user_id,q.reason,q.sent_at,u.email,u.first_name FROM account_mail_queue q JOIN user_account u ON u.id=q.user_id WHERE q.event_id=? AND u.is_active=1 FOR UPDATE",eventId);
  if(rows.isEmpty()||rows.getFirst().get("sent_at")!=null)throw new ResponseStatusException(HttpStatus.GONE,"Mail request no longer active");var row=rows.getFirst();long userId=WorkAccess.number(row.get("user_id"));String purpose="EMAIL_VERIFICATION".equals(row.get("reason"))?"EMAIL_VERIFICATION":"PASSWORD_SETUP";String actionUrl="EMAIL_VERIFICATION".equals(purpose)?passwords.verificationLink(userId):passwords.link(userId);return Map.of("to",row.get("email"),"name",row.get("first_name"),"purpose",purpose,"actionUrl",actionUrl);
 }
 @PostMapping("/mail/{eventId}/delivered") public void delivered(@PathVariable String eventId){jdbc.update("UPDATE account_mail_queue SET sent_at=COALESCE(sent_at,CURRENT_TIMESTAMP) WHERE event_id=?",eventId);}
}
