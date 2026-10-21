package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.components.OrgComponent;
import com.greatleyposhley.professy.entities.DocAuditLog;
import com.greatleyposhley.professy.services.DocAuditLogService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docs/audit-logs")
public class DocAuditLogController {

    private final DocAuditLogService service;
    private final OrgComponent orgComponent;

    public DocAuditLogController(
            DocAuditLogService service, OrgComponent orgComponent) {
        this.service = service;
        this.orgComponent = orgComponent;
    }

    @GetMapping
    public List<DocAuditLog> getAll(Authentication authentication) {
        return service.getAll(orgComponent.getOrgId(authentication));
    }

    @GetMapping("/{id}")
    public DocAuditLog getById(
            Authentication authentication, @PathVariable Long id) {
        return service.getById(orgComponent.getOrgId(authentication), id);
    }
}