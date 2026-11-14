package com.greatleyposhley.professy.collaboration;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/** Response hardening for bearer APIs; intentionally does not alter authored document contents. */
@Component @Order(Ordered.HIGHEST_PRECEDENCE+10)
public class ApiSafetyFilter extends OncePerRequestFilter {
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException {
  response.setHeader("X-Content-Type-Options","nosniff");response.setHeader("Referrer-Policy","no-referrer");response.setHeader("X-Frame-Options","DENY");
  if(request.isSecure())response.setHeader("Strict-Transport-Security","max-age=31536000; includeSubDomains");
  if(request.getRequestURI().startsWith("/api/")||request.getRequestURI().startsWith("/auth/")||request.getRequestURI().startsWith("/notifications")) {
   response.setHeader("Cache-Control","no-store");
   response.setHeader("Content-Security-Policy","default-src 'none'; frame-ancestors 'none'; base-uri 'none'");
  }
  if("TRACE".equals(request.getMethod())){response.sendError(405);return;}
  chain.doFilter(request,response);
 }
}
