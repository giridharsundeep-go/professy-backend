package com.greatleyposhley.professy.collaboration;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
@Component public class DeliverySchema {
 private final JdbcTemplate jdbc;
 public DeliverySchema(JdbcTemplate jdbc){this.jdbc=jdbc;}
 @PostConstruct void initialize(){new ResourceDatabasePopulator(new ClassPathResource("db/V20261104__delivery.sql"),new ClassPathResource("db/V20261105__ticket_flow.sql"),new ClassPathResource("db/V20261109__support_desk.sql"),new ClassPathResource("db/V20261110__agent_execution.sql")).execute(Objects.requireNonNull(jdbc.getDataSource()));}
}
