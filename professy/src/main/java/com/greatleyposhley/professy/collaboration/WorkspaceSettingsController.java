package com.greatleyposhley.professy.collaboration;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;

@RestController @RequestMapping("/api/workspace/settings")
public class WorkspaceSettingsController {
 private final WorkAccess access;private final WorkspacePolicy policy;private final WorkspaceRealtime realtime;
 public WorkspaceSettingsController(WorkAccess access,WorkspacePolicy policy,WorkspaceRealtime realtime){this.access=access;this.policy=policy;this.realtime=realtime;}
 @GetMapping public Object settings(){var user=access.actor();return Map.of("statuses",policy.statuses(user.getOrgId()),"roles",policy.roles(user.getOrgId()),"permissions",WorkspacePolicy.permissions(),"effective",policy.effective(user),"assignedRoles",policy.assigned(user.getOrgId(),user.getId()),"canManageAccess",access.admin(user));}
 public record UserAccess(List<String> roles,Map<String,Boolean> overrides){}
 @GetMapping("/users/{id}")public Object user(@PathVariable long id){if(id!=access.actor().getId())policy.require(access.actor(),"USERS.READ");return policy.userAccess(access.actor(),id);}
 @PutMapping("/users/{id}")public Object user(@PathVariable long id,@RequestBody UserAccess body){policy.assign(access.actor(),id,body.roles(),body.overrides());return policy.userAccess(access.actor(),id);}
 public record RoleBody(String name,List<String> permissions){}
 @PutMapping("/roles/{code}")public void role(@PathVariable String code,@RequestBody RoleBody body){policy.saveRole(access.actor(),code,body.name(),body.permissions());}
 @PutMapping("/statuses/{code}")public void status(@PathVariable String code,@RequestBody Map<String,Object> body){policy.saveStatus(access.actor(),code,body);}
 @GetMapping(value="/events",produces="text/event-stream")public org.springframework.web.servlet.mvc.method.annotation.SseEmitter events(){return realtime.subscribe(access.actor());}
}
