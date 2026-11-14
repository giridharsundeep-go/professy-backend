package com.greatleyposhley.professy.collaboration;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/sprints/{id}/lifecycle")
public class SprintLifecycleController {
 private final SprintLifecycleCoordinator lifecycle;private final WorkAccess access;
 public SprintLifecycleController(SprintLifecycleCoordinator lifecycle,WorkAccess access){this.lifecycle=lifecycle;this.access=access;}
 @GetMapping public Object get(@PathVariable long id){var row=lifecycle.sprint(access.actor().getOrgId(),id);access.project(WorkAccess.number(row.get("project_id")));return row;}
 @PutMapping public Object settings(@PathVariable long id,@RequestBody Map<String,Object> body){access.require("SPRINTS.UPDATE");return lifecycle.settings(access.actor().getOrgId(),id,access.actor().getId(),body);}
 @PostMapping("/start")public Object start(@PathVariable long id){access.require("SPRINTS.UPDATE");return lifecycle.start(access.actor().getOrgId(),id,access.actor().getId(),false);}
 @PostMapping("/close")public Object close(@PathVariable long id){access.require("SPRINTS.UPDATE");return lifecycle.close(access.actor().getOrgId(),id,access.actor().getId(),false);}
}
