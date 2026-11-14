package com.greatleyposhley.professy.collaboration;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
public class ServiceAuthenticationFilter extends OncePerRequestFilter {
 private final String token;public ServiceAuthenticationFilter(String token){this.token=token;}
 @Override protected boolean shouldNotFilter(HttpServletRequest r){return !r.getRequestURI().startsWith("/api/internal/");}
 @Override protected void doFilterInternal(HttpServletRequest r,HttpServletResponse response,FilterChain chain)throws IOException,ServletException{
  String provided=Optional.ofNullable(r.getHeader("X-Service-Token")).orElse("");
  if(token.length()<32||!MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8),provided.getBytes(StandardCharsets.UTF_8))){response.sendError(401);return;}
  SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("event-service",null,List.of(new SimpleGrantedAuthority("ROLE_EVENT_SERVICE"))));chain.doFilter(r,response);
 }
}
