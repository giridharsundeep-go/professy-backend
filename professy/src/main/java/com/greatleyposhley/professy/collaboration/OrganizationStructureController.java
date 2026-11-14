package com.greatleyposhley.professy.collaboration;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@RestController @RequestMapping("/api/workspace/organization")
public class OrganizationStructureController {
 private final WorkAccess access;private final OrganizationStructure structure;
 public OrganizationStructureController(WorkAccess access,OrganizationStructure structure){this.access=access;this.structure=structure;}
 @GetMapping("/units")public Object list(){access.require("USERS.READ");return structure.units(access.actor().getOrgId());}
 @PostMapping("/units")@Transactional public Object create(@RequestBody Map<String,Object> body){access.require("USERS.UPDATE");String type=Objects.toString(body.get("type"),""),name=Objects.toString(body.get("name"),"").trim();if(!Set.of("DEPARTMENT","FUNCTION","DIVISION").contains(type)||name.isBlank()||name.length()>120)throw new IllegalArgumentException("Choose a unit type and a name up to 120 characters");long parent=WorkAccess.number(body.get("parentId"));if(parent>0&&access.jdbc.queryForObject("SELECT COUNT(*) FROM organization_units WHERE id=? AND org_id=? AND active=1",Integer.class,parent,access.actor().getOrgId())==0)throw new IllegalArgumentException("Parent unit must belong to this organization");access.jdbc.update("INSERT INTO organization_units(org_id,unit_type,name,parent_id,active) VALUES(?,?,?,?,?)",access.actor().getOrgId(),type,name,parent>0?parent:null,!Boolean.FALSE.equals(body.get("active")));access.policy.event(access.actor(),"ORGANIZATION_UPDATED",0);return list();}
 @PutMapping("/units/{id}")@Transactional public Object update(@PathVariable long id,@RequestBody Map<String,Object> body){access.require("USERS.UPDATE");String name=Objects.toString(body.get("name"),"").trim();if(name.isBlank()||name.length()>120)throw new IllegalArgumentException("Enter a name up to 120 characters");if(access.jdbc.update("UPDATE organization_units SET name=?,active=? WHERE id=? AND org_id=?",name,!Boolean.FALSE.equals(body.get("active")),id,access.actor().getOrgId())==0)throw new ResponseStatusException(HttpStatus.NOT_FOUND);access.policy.event(access.actor(),"ORGANIZATION_UPDATED",0);return list();}
}
