package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.dtos.OrgValidateRequest;
import com.greatleyposhley.professy.entities.dtos.OrgValidateResponse;
import com.greatleyposhley.professy.dtos.VerifyEmailRequest;
import com.greatleyposhley.professy.dtos.VerifyEmailResponse;
import com.greatleyposhley.professy.entities.Org;
import com.greatleyposhley.professy.services.OrgService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organisation")
@CrossOrigin(origins = "*")
public class OrgController {

    private final OrgService orgService;

    public OrgController(OrgService orgService) {
        this.orgService = orgService;
    }

    @PostMapping("/validate")
    public ResponseEntity<OrgValidateResponse> validateOrganisation(@RequestBody OrgValidateRequest request) {
        OrgValidateResponse response = orgService.validateOrganisation(request.getSlug());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Org> getOrgBySlug(@PathVariable String slug) {
        Org org = orgService.getOrgBySlug(slug);
        return ResponseEntity.ok(org);
    }
}