
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
            HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
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

                        // Public authentication APIs
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
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

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
