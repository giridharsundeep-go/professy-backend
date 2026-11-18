package com.greatleyposhley.professy.collaboration;
import com.greatleyposhley.professy.entities.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;

@Service
public class OrganizationStructure {
 private final JdbcTemplate jdbc;
 public OrganizationStructure(JdbcTemplate jdbc){this.jdbc=jdbc;}
 public List<Map<String,Object>> units(long org){return jdbc.queryForList("SELECT id,unit_type AS type,name,parent_id AS parentId,active FROM organization_units WHERE org_id=? ORDER BY unit_type,name",org);}
 public void validate(UserAccount user){check(user.getOrgId(),"DEPARTMENT",user.getDepartmentId());check(user.getOrgId(),"FUNCTION",user.getFunctionId());check(user.getOrgId(),"DIVISION",user.getDivisionId());}
 public void validateChanges(UserAccount user,UserAccount previous){
  if(user.isDepartmentProvided()&&!Objects.equals(user.getDepartmentId(),previous.getDepartmentId()))check(user.getOrgId(),"DEPARTMENT",user.getDepartmentId());
  if(user.isFunctionProvided()&&!Objects.equals(user.getFunctionId(),previous.getFunctionId()))check(user.getOrgId(),"FUNCTION",user.getFunctionId());
  if(user.isDivisionProvided()&&!Objects.equals(user.getDivisionId(),previous.getDivisionId()))check(user.getOrgId(),"DIVISION",user.getDivisionId());
 }
 private void check(long org,String type,Long id){if(id!=null&&jdbc.queryForObject("SELECT COUNT(*) FROM organization_units WHERE id=? AND org_id=? AND unit_type=? AND active=1",Integer.class,id,org,type)==0)throw new IllegalArgumentException("Select an active "+type.toLowerCase()+" in your organization");}
 public List<UserAccount> names(List<UserAccount> users){if(users.isEmpty())return users;var names=new HashMap<Long,String>();for(var unit:units(users.getFirst().getOrgId()))names.put(WorkAccess.number(unit.get("id")),unit.get("name").toString());for(var user:users){user.setDepartmentName(names.get(user.getDepartmentId()));user.setFunctionName(names.get(user.getFunctionId()));user.setDivisionName(names.get(user.getDivisionId()));}return users;}
}
