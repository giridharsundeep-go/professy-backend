
package com.greatleyposhley.professy.components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey secretKey;
    private final com.greatleyposhley.professy.repositories.UserAccountRepository users;

    public JwtAuthenticationFilter(
            @Value("${jwt.secret}") String secret, com.greatleyposhley.professy.repositories.UserAccountRepository users) {
        this.users=users;
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // All authentication endpoints are public.
        return request.getRequestURI().startsWith("/api/meeting-runtime/") || java.util.Set.of("/api/auth/login","/api/auth/handoff/consume","/api/auth/register","/api/auth/password/request","/api/auth/password/reset","/api/auth/email/verify","/api/auth/organisation/validate").contains(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // No token: continue. Spring Security will enforce access rules.
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();

            if (email == null || email.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid JWT subject"
                );
                return;
            }

            var user=users.findByEmail(email).filter(u->Boolean.TRUE.equals(u.getIsActive())).orElseThrow();
            int credentialVersion=claims.get("credentialVersion",Integer.class)==null?0:claims.get("credentialVersion",Integer.class);
            if(user.getCredentialVersion()!=credentialVersion || user.getPasswordSetupRequired())throw new IllegalArgumentException("Session revoked");
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(user.getOrgRole()==com.greatleyposhley.professy.entities.OrgRole.SUPER_USER?"ROLE_ORG_ADMIN":"ROLE_USER")
                            )
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            new org.springframework.security.web.context.RequestAttributeSecurityContextRepository()
                    .saveContext(SecurityContextHolder.getContext(),request,response);

        } catch (org.springframework.dao.DataAccessException ex) {
            // A busy/unavailable database does not invalidate an otherwise valid account session.
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.setContentType("application/json");
            response.setHeader("Retry-After", "3");
            response.setHeader("Cache-Control", "no-store");
            response.getWriter().write("{\"message\":\"Account validation is temporarily unavailable. Try again shortly.\"}");
            return;
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired JWT"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
