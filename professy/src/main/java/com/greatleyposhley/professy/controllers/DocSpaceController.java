
package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.components.OrgComponent;
import com.greatleyposhley.professy.entities.DocSpace;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import com.greatleyposhley.professy.services.DocSpaceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/docs/spaces")
@CrossOrigin(origins = "*")
public class DocSpaceController {

    private final DocSpaceService service;
    private final OrgComponent orgComponent;
    private final UserAccountRepository userAccountRepository;

    public DocSpaceController(
            DocSpaceService service,
            OrgComponent orgComponent,
            UserAccountRepository userAccountRepository) {
        this.service = service;
        this.orgComponent = orgComponent;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping
    public List<DocSpace> getAll(Authentication authentication) {
        Long orgId = orgComponent.getOrgId(authentication);
        return service.getAll(orgId);
    }

    @GetMapping("/{id}")
    public DocSpace getById(
            Authentication authentication,
            @PathVariable Long id) {

        Long orgId = orgComponent.getOrgId(authentication);
        return service.getById(orgId, id);
    }

    @PostMapping
    public ResponseEntity<DocSpace> create(
            Authentication authentication,
            @RequestBody DocSpace input) {

        Long orgId = orgComponent.getOrgId(authentication);
        input.setOrgId(orgId);

        resolveRequestedUser(input.getRequestedUserId(), input::setUserAccount);

        DocSpace created = service.create(input);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public DocSpace update(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody DocSpace input) {

        Long orgId = orgComponent.getOrgId(authentication);

        // Prevent the request body from changing the tenant.
        input.setOrgId(orgId);
        resolveRequestedUser(input.getRequestedUserId(), input::setUserAccount);

        return service.update(orgId, id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable Long id) {

        Long orgId = orgComponent.getOrgId(authentication);
        service.delete(orgId, id);

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
