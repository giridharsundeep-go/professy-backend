package com.greatleyposhley.professy.components;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.*;
import java.util.*;

/** Legacy controllers catch database errors locally; keep those details out of HTTP responses too. */
@RestControllerAdvice public class ApiErrorRedaction implements ResponseBodyAdvice<Object> {
 public boolean supports(MethodParameter method,Class<? extends HttpMessageConverter<?>> converter){return true;}
 public Object beforeBodyWrite(Object body,MethodParameter method,MediaType media,Class<? extends HttpMessageConverter<?>> converter,ServerHttpRequest request,ServerHttpResponse response){
  int status=response instanceof ServletServerHttpResponse servlet?servlet.getServletResponse().getStatus():200;
  if(status<400||!(body instanceof Map<?,?> map))return body;
  String message=Objects.toString(map.get("message"),"");
  boolean internal=status>=500||message.matches("(?is).*(JDBC|SQLState|SQL \\[|org\\.hibernate|constraint \\[|INSERT INTO|SELECT .+ FROM).*" );
  if(!internal)return body;
  var clean=new LinkedHashMap<String,Object>();map.forEach((key,value)->{if(!Set.of("trace","stackTrace","exception","details","data","message","error").contains(key.toString()))clean.put(key.toString(),value);});
  clean.put("message",status>=500?"The service could not complete this request. Please retry.":"Check the supplied values and try again.");
  if(map.containsKey("error"))clean.put("error",status>=500?"Service error":"Invalid request");return clean;
 }
}
