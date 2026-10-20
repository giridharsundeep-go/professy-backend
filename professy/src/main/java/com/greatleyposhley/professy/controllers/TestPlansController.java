package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.TestPlans;
import com.greatleyposhley.professy.services.TestPlansService;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-plans")
@CrossOrigin(origins = "*")
public class TestPlansController {

    private final TestPlansService testPlansService;
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public TestPlansController(TestPlansService testPlansService,
                               UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        this.testPlansService = testPlansService;
    }


    // ============================================================
    // GET ALL TEST PLANS
    // ============================================================

    @GetMapping
    public ResponseEntity<List<TestPlans>> getAllTestPlans(Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                testPlansService.getAllTestPlans(orgId)
        );
    }


    // ============================================================
    // GET TEST PLAN BY ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<TestPlans> getTestPlanById(@PathVariable Long id,
                                                     Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                testPlansService.getTestPlanById(orgId,
                        id)
        );
    }


    // ============================================================
    // GET TEST PLANS BY PROJECT
    // ============================================================

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TestPlans>> getTestPlansByProjectId(@PathVariable Long projectId,
                                                                   Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                testPlansService.getTestPlansByProjectId(orgId,
                        projectId)
        );
    }


    // ============================================================
    // CREATE TEST PLAN
    // ============================================================

    @PostMapping
    public ResponseEntity<TestPlans> createTestPlan(@RequestBody TestPlans testPlan,
                                                    Authentication authentication) {
        Long orgId = getOrgId(authentication);

        TestPlans saved =
                testPlansService.saveTestPlan(orgId,
                        testPlan);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }


    // ============================================================
    // UPDATE TEST PLAN
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<TestPlans> updateTestPlan(@PathVariable Long id,
                                                    @RequestBody TestPlans testPlan,
                                                    Authentication authentication) {
        Long orgId = getOrgId(authentication);

        testPlan.setId(id);

        TestPlans updated =
                testPlansService.saveTestPlan(orgId,
                        testPlan);

        return ResponseEntity.ok(updated);
    }


    // ============================================================
    // DELETE TEST PLAN
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestPlan(@PathVariable Long id,
                                               Authentication authentication) {
        Long orgId = getOrgId(authentication);

        testPlansService.deleteTestPlan(orgId,
                id);

        return ResponseEntity.noContent().build();
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

