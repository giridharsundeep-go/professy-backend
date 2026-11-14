package com.greatleyposhley.professy.collaboration;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
/** Gateway to the separately deployed notification service. */
@RestController @RequestMapping("/api/notifications")
public class NotificationController {
 private final RestClient client;private final WorkAccess access;private final TicketTitles titles;
 public NotificationController(@Value("${app.notification-service-url:http://127.0.0.1:8082}")String url,WorkAccess access,TicketTitles titles){this.titles=titles;var factory=new org.springframework.http.client.SimpleClientHttpRequestFactory();factory.setConnectTimeout(3000);factory.setReadTimeout(5000);this.client=RestClient.builder().baseUrl(url).requestFactory(factory).build();this.access=access;}
 @GetMapping public Object list(@RequestParam(defaultValue="0") long before,HttpServletRequest request){access.actor();try{Object result=client.get().uri("/notifications?before="+before).header("Authorization",request.getHeader("Authorization")).retrieve().body(Object.class);if(result instanceof java.util.Map<?,?> root && root.get("items") instanceof java.util.List<?> rows){for(Object value:rows)if(value instanceof java.util.Map row && !java.util.Set.of("CHAT","CALENDAR","MAIL").contains(row.get("ticket_type"))){row.put("title",titles.title(String.valueOf(row.get("ticket_type")),WorkAccess.number(row.get("ticket_id")),access.actor().getOrgId(),null));row.put("body","Ticket activity");}}return result;}catch(Exception error){throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Notifications are temporarily unavailable");}}
 @PutMapping("/{id}/read") public void read(@PathVariable long id,HttpServletRequest request){forward("/notifications/"+id+"/read",request);}
 @PutMapping("/read-all") public void readAll(HttpServletRequest request){forward("/notifications/read-all",request);}
 private void forward(String path,HttpServletRequest request){access.actor();try{client.put().uri(path).header("Authorization",request.getHeader("Authorization")).retrieve().toBodilessEntity();}catch(Exception error){throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Unable to update notifications");}}
}
