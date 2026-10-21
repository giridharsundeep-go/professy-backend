package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.components.OrgComponent;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.web.server.ResponseStatusException;
import com.greatleyposhley.professy.entities.DocPageVersion;
import com.greatleyposhley.professy.services.DocPageVersionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docs/page-versions")
public class DocPageVersionController {

    private final DocPageVersionService service;
    private final OrgComponent orgComponent;

        private final UserAccountRepository userAccountRepository;
public DocPageVersionController(
            DocPageVersionService service, OrgComponent orgComponent, UserAccountRepository userAccountRepository) {
        this.service = service;
        this.orgComponent = orgComponent;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping
    public List<DocPageVersion> getAll(Authentication authentication) {
        return service.getAll(orgComponent.getOrgId(authentication));
    }

    @GetMapping("/{id}")
    public DocPageVersion getById(
            Authentication authentication, @PathVariable Long id) {
        return service.getById(orgComponent.getOrgId(authentication), id);
    }

    @PostMapping
    public ResponseEntity<DocPageVersion> create(
            Authentication authentication,
            @RequestBody DocPageVersion input) {
        Long orgId = orgComponent.getOrgId(authentication);
        resolveRequestedUser(input.getRequestedUserId(), input::setUserId);
        DocPageVersion created = service.create(orgId, input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            Authentication authentication, @PathVariable Long id) {
        service.delete(orgComponent.getOrgId(authentication), id);
        return ResponseEntity.noContent().build();
    }

    private void resolveRequestedUser(Long userId, java.util.function.Consumer<UserAccount> setter) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user_id is required");
        }
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + userId));
        setter.accept(user);
    }
}