package com.greatleyposhley.professy.collaboration;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EmailVerificationSchema {
    private final JdbcTemplate jdbc;

    public EmailVerificationSchema(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void initialize() {
        new ResourceDatabasePopulator(
                new ClassPathResource("db/V20261112__email_verification.sql")
        ).execute(Objects.requireNonNull(jdbc.getDataSource()));
    }
}
