package com.greatleyposhley.professy.collaboration;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.*;
import java.util.*;

/** Scope list responses as well as direct ticket URLs; hiding controls alone is not authorization. */
@RestControllerAdvice @org.springframework.core.annotation.Order(0)
public class ProjectResponseScope implements ResponseBodyAdvice<Object>{
    private final WorkAccess access;private final ObjectMapper json;
    public ProjectResponseScope(WorkAccess access,ObjectMapper json){this.access=access;this.json=json;}
    public boolean supports(MethodParameter type,Class<? extends HttpMessageConverter<?>> converter){return true;}
    public Object beforeBodyWrite(Object body,MethodParameter type,MediaType contentType,Class<? extends HttpMessageConverter<?>> converter,ServerHttpRequest request,ServerHttpResponse response){
        String path=request.getURI().getPath();if(body==null||!path.startsWith("/api/")||path.startsWith("/api/auth/")||path.startsWith("/api/tickets/")||!path.matches("/api/(v1/)?(projects|epics|stories|tasks|issues|testcases|sprints|releases)(/.*)?")||access.admin(access.actor()))return body;
        var allowed=access.projects(access.actor());JsonNode root=json.valueToTree(body);String resource=path.replaceFirst("^/api/(v1/)?","").split("/")[0];var nested=java.util.regex.Pattern.compile("/projects/[0-9]+/(epics|stories|tasks|issues|testcases)").matcher(path);if(nested.find())resource=nested.group(1);
        JsonNode scoped=scope(root,allowed,resource,true);if(scoped==null)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Project access denied");return scoped;
    }
    private JsonNode scope(JsonNode node,Set<Long> allowed,String resource,boolean top){
        if(node.isArray()){
            ArrayNode result=json.createArrayNode();for(JsonNode row:node){var filtered=scope(row,allowed,resource,true);if(filtered!=null)result.add(filtered);}return result;
        }
        if(!node.isObject())return node;
        long project=node.path("projectId").asLong(node.path("project_id").asLong(node.path("project").path("id").asLong(0)));
        if(project==0&&resource.equals("projects")&&node.has("id"))project=node.path("id").asLong();
        if(project==0&&resource.equals("tasks")){
            long story=node.path("storyId").asLong(node.path("story_id").asLong(node.path("story").path("id").asLong(0)));
            if(story>0){var ids=access.jdbc.queryForList("SELECT project_id FROM stories WHERE id=? AND org_id=?",Long.class,story,access.actor().getOrgId());if(!ids.isEmpty())project=ids.getFirst();}
        }
        if(project>0&&!allowed.contains(project))return null;
        ObjectNode result=(ObjectNode)node.deepCopy();
        for(String key:List.of("content","data","items","results","_embedded"))if(node.has(key)){JsonNode child=scope(node.get(key),allowed,resource,true);result.set(key,child==null?json.createArrayNode():child);}
        if(node.has("_embedded")){ObjectNode embedded=(ObjectNode)result.get("_embedded");for(String key:new ArrayList<>(embedded.propertyNames()))embedded.set(key,scope(embedded.get(key),allowed,resource,true));}
        // Nested children with their own project references must also stay inside the scope.
        if(project==0&&node.has("id")&&Set.of("epics","stories","tasks","issues","testcases","sprints","releases").contains(resource))return null;
        return result;
    }
}
