package com.greatleyposhley.professy.collaboration;
import org.springframework.stereotype.Component;import org.springframework.jdbc.core.JdbcTemplate;import org.springframework.core.io.ClassPathResource;import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;import jakarta.annotation.PostConstruct;import java.util.Objects;
@Component @org.springframework.context.annotation.DependsOn({"workspacePolicy","organizationStructure"})
public class WorkspaceAppsSchema {private final JdbcTemplate jdbc;public WorkspaceAppsSchema(JdbcTemplate jdbc){this.jdbc=jdbc;}@PostConstruct void initialize(){new ResourceDatabasePopulator(new ClassPathResource("db/V20261103__workspace_apps.sql")).execute(Objects.requireNonNull(jdbc.getDataSource()));}}
