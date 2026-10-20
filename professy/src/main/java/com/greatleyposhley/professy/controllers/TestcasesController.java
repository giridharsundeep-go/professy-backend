package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.Testcases;
import com.greatleyposhley.professy.services.TestcasesService;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/testcases")
@CrossOrigin(origins = "*")
public class TestcasesController {

    private final TestcasesService testcasesService;
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public TestcasesController(TestcasesService testcasesService,
                               UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        this.testcasesService = testcasesService;
    }

    @GetMapping
    public ResponseEntity<List<Testcases>> getAllTestcases(@RequestParam(required = false) Long projectId,
                                                           Authentication authentication) {
        Long orgId = getOrgId(authentication);
        if (projectId != null) {
            return ResponseEntity.ok(testcasesService.getTestcasesByProjectId(orgId,
                    projectId));
        }
        return ResponseEntity.ok(testcasesService.getAllTestcases(orgId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Testcases> getTestcaseById(@PathVariable Long id,
                                                     Authentication authentication) {
        Long orgId = getOrgId(authentication);
        return ResponseEntity.ok(testcasesService.getTestcaseById(orgId,
                id));
    }

    @PostMapping
    public ResponseEntity<Testcases> createTestcase(@RequestBody Testcases testcase,
                                                    Authentication authentication) {
        Long orgId = getOrgId(authentication);
        Testcases created = testcasesService.saveTestcase(orgId,
                testcase);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Testcases> updateTestCase(@PathVariable Long id, @RequestBody Testcases testcase,
                                                    Authentication authentication) {
        Long orgId = getOrgId(authentication);
        testcase.setId(id);
        return ResponseEntity.ok(testcasesService.saveTestcase(orgId,
                testcase));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestcase(@PathVariable Long id,
                                               Authentication authentication) {
        Long orgId = getOrgId(authentication);
        testcasesService.deleteTestcase(orgId,
                id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{testCaseId}/assign")
    public ResponseEntity<Testcases> assignTestCase(@PathVariable Long testCaseId,
                                                    @RequestBody Map<String, Object> request,
                                                    Authentication authentication) {
        Long orgId = getOrgId(authentication);

        String type = String.valueOf(request.get("type"));
        Long itemId = Long.valueOf(
                String.valueOf(request.get("id"))
        );

        Testcases testcase =
                testcasesService.assignTestCase(
                        orgId,
                        testCaseId,
                        type,
                        itemId
                );

        return ResponseEntity.ok(testcase);
    }

    @PutMapping("/{testCaseId}/unassign")
    public ResponseEntity<Testcases> unassignTestCase(@PathVariable Long testCaseId,
                                                      @RequestBody Map<String, Object> request,
                                                      Authentication authentication) {
        Long orgId = getOrgId(authentication);

        String type = String.valueOf(request.get("type"));
        Long itemId = Long.valueOf(
                String.valueOf(request.get("id"))
        );

        Testcases testcase =
                testcasesService.unassignTestCase(
                        orgId,
                        testCaseId,
                        type,
                        itemId
                );

        return ResponseEntity.ok(testcase);
    }



    private Long getOrgId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentication required");
        }

        String email = authentication.getName();

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Authenticated user is required");
        }

        return userAccountRepository
                .findByEmail(email.trim().toLowerCase())
                .map(account -> account.getOrgId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Authenticated user not found")
                );
    }
}
