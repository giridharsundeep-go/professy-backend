package com.greatleyposhley.professy.collaboration;
import java.util.*;
/** Pure validation shared by the human and worker contracts. */
public final class DeliveryRules {
 public static final Set<String> SKILLS=RuntimeSkills.IDS;
 public static String text(Object value,int maximum){String s=Objects.toString(value,"").trim();if(s.length()>maximum)throw new IllegalArgumentException("Field exceeds "+maximum+" characters");return s;}
 public static String required(Object value,int max){String s=text(value,max);if(s.isBlank())throw new IllegalArgumentException("Required field is empty");return s;}
 public static String choice(Object value,Set<String> choices){String s=required(value,100);if(!choices.contains(s))throw new IllegalArgumentException("Unsupported option");return s;}
 public static Map<String,String> files(Object value){
  if(!(value instanceof List<?> list)||list.isEmpty()||list.size()>12)throw new IllegalArgumentException("Provide 1–12 evidence files");
  var files=new LinkedHashMap<String,String>();int total=0;
  for(Object item:list){if(!(item instanceof Map<?,?> file))throw new IllegalArgumentException("Invalid evidence");String path=required(file.get("path"),120),content=text(file.get("content"),100000);
   if(!path.matches("[A-Za-z0-9_-]+\\.(py|md|txt|json)")||files.putIfAbsent(path,content)!=null)throw new IllegalArgumentException("Unsafe or duplicate evidence filename");total+=content.length();}
  if(total>300000||!files.containsKey("implementation.py")||!files.containsKey("test_implementation.py"))throw new IllegalArgumentException("Implementation and test evidence are required (300KB limit)");
  return files;
 }
 public static Map<String,Object> graph(Object value,Set<Long> services){
  if(!(value instanceof Map<?,?> graph)||!(graph.get("nodes") instanceof List<?> nodes)||!(graph.get("edges") instanceof List<?> edges)||nodes.size()>150||edges.size()>400)throw new IllegalArgumentException("Invalid architecture graph");
  var ids=new HashSet<Long>();var cleanNodes=new ArrayList<Map<String,Object>>();var cleanEdges=new ArrayList<Map<String,Object>>();
  for(Object n:nodes){if(!(n instanceof Map<?,?> node))throw new IllegalArgumentException("Invalid node");long id=WorkAccess.number(node.get("id"));double x=coordinate(node.get("x")),y=coordinate(node.get("y"));if(!services.contains(id)||!ids.add(id))throw new IllegalArgumentException("Choose unique services from this project");cleanNodes.add(Map.of("id",id,"x",x,"y",y));}
  var seen=new HashSet<String>();for(Object e:edges){if(!(e instanceof Map<?,?> edge))throw new IllegalArgumentException("Invalid edge");long from=WorkAccess.number(edge.get("from")),to=WorkAccess.number(edge.get("to"));String type=choice(edge.get("type"),Set.of("HTTP","EVENT","SQL","DEPENDS_ON"));if(from==to||!ids.contains(from)||!ids.contains(to)||!seen.add(from+":"+to+":"+type))throw new IllegalArgumentException("Choose distinct services with unique connections");cleanEdges.add(Map.of("from",from,"to",to,"type",type));}
  return Map.of("nodes",cleanNodes,"edges",cleanEdges);
 }
 private static double coordinate(Object value){if(!(value instanceof Number n)||!Double.isFinite(n.doubleValue())||n.doubleValue()<0||n.doubleValue()>5000)throw new IllegalArgumentException("Coordinates must be between 0 and 5000");return n.doubleValue();}
 private DeliveryRules(){}
}
