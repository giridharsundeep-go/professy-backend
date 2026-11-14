
package com.greatleyposhley.professy.components;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, com.greatleyposhley.professy.collaboration.WorkAccess access, org.springframework.transaction.PlatformTransactionManager manager, jakarta.persistence.EntityManager entityManager, tools.jackson.databind.ObjectMapper json, com.greatleyposhley.professy.collaboration.TicketActors ticketActors, @org.springframework.beans.factory.annotation.Value("${app.internal-service-token:}") String serviceToken) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .securityContext(context -> context.securityContextRepository(new org.springframework.security.web.context.RequestAttributeSecurityContextRepository()))
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request,response,error) -> securityError(response,401,"Sign in to continue"))
                        .accessDeniedHandler((request,response,error) -> securityError(response,403,"Access denied")))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        // Allow CORS preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Service contracts are inaccessible to browser accounts.
                        .requestMatchers("/api/internal/**").hasRole("EVENT_SERVICE")
                        // Meeting runtime credentials are validated by the scoped runtime controller.
                        .requestMatchers("/api/meeting-runtime/**", "/api/delivery-worker/**").permitAll()
                        // Public authentication APIs
                        .requestMatchers("/api/auth/login","/api/auth/handoff/consume","/api/auth/register","/api/auth/organisation/validate","/api/auth/password/request","/api/auth/password/reset","/api/auth/email/verify").permitAll()
                        .requestMatchers("/api/auth/user/**","/api/auth/organisation/**").hasRole("ORG_ADMIN")
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Sanitized, unguessable public-by-link avatar images only.
                        .requestMatchers(HttpMethod.GET, "/media/avatars/*").permitAll()
                        // User account APIs
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/organizations/*/users/**").authenticated()

                        // Existing application APIs
                        .requestMatchers("/api/roles/**").authenticated()
                        .requestMatchers("/api/epics/**").authenticated()
                        .requestMatchers("/api/stories/**").authenticated()
                        .requestMatchers("/api/products/**").authenticated()
                        .requestMatchers("/api/projects/**").authenticated()
                        .requestMatchers("/api/sprints/**").authenticated()
                        .requestMatchers("/api/tasks/**").authenticated()
                        .requestMatchers("/api/testcases/**").authenticated()

                        // Developer documentation APIs
                        .requestMatchers("/api/docs/spaces/*/folders").authenticated()
                        .requestMatchers("/api/docs/spaces/*/folders/**").authenticated()
                        .requestMatchers("/api/docs/folders/**").authenticated()
                        .requestMatchers("/api/docs/spaces/**").authenticated()
                        .requestMatchers("/api/docs/pages/**").authenticated()
                        .requestMatchers("/api/docs/page-versions/**").authenticated()
                        .requestMatchers("/api/docs/services/**").authenticated()
                        .requestMatchers("/api/docs/api-versions/**").authenticated()
                        .requestMatchers("/api/docs/permissions/**").authenticated()
                        .requestMatchers("/api/docs/audit-logs/**").authenticated()

                        // Protect all remaining endpoints
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        http.addFilterAfter(new com.greatleyposhley.professy.collaboration.ServiceAuthenticationFilter(serviceToken), JwtAuthenticationFilter.class);
        http.addFilterAfter(new com.greatleyposhley.professy.collaboration.WorkRequestFilter(access,manager,entityManager,json,ticketActors), JwtAuthenticationFilter.class);
        return http.build();
    }

    private static void securityError(jakarta.servlet.http.HttpServletResponse response,int status,String message)throws java.io.IOException {
        if(response.isCommitted())return;
        response.setStatus(status);
        response.setContentType("application/json");
        response.setHeader("Cache-Control","no-store");
        response.getWriter().write("{\"message\":\""+message+"\"}");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
