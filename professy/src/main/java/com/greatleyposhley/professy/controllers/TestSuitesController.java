package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.TestSuites;
import com.greatleyposhley.professy.services.TestSuitesService;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-suites")
@CrossOrigin(origins = "*")
public class TestSuitesController {

    private final TestSuitesService testSuitesService;
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public TestSuitesController(TestSuitesService testSuitesService,
                                UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        this.testSuitesService = testSuitesService;
    }


    // ============================================================
    // GET ALL TEST SUITES
    // ============================================================

    @GetMapping
    public ResponseEntity<List<TestSuites>> getAllTestSuites(Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                testSuitesService.getAllTestSuites(orgId)
        );
    }


    // ============================================================
    // GET TEST SUITE BY ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<TestSuites> getTestSuiteById(@PathVariable Long id,
                                                       Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                testSuitesService.getTestSuiteById(orgId,
                        id)
        );
    }


    // ============================================================
    // GET TEST SUITES BY TEST PLAN
    // ============================================================

    @GetMapping("/test-plan/{testPlanId}")
    public ResponseEntity<List<TestSuites>> getTestSuitesByTestPlanId(@PathVariable Long testPlanId,
                                                                      Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                testSuitesService.getTestSuitesByTestPlanId(orgId,
                        testPlanId)
        );
    }


    // ============================================================
    // GET TEST SUITES BY PROJECT
    // ============================================================

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TestSuites>> getTestSuitesByProjectId(@PathVariable Long projectId,
                                                                     Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                testSuitesService.getTestSuitesByProjectId(orgId,
                        projectId)
        );
    }


    // ============================================================
    // CREATE TEST SUITE
    // ============================================================

    @PostMapping
    public ResponseEntity<TestSuites> createTestSuite(@RequestBody TestSuites testSuite,
                                                      Authentication authentication) {
        Long orgId = getOrgId(authentication);

        TestSuites saved =
                testSuitesService.saveTestSuite(orgId,
                        testSuite);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }


    // ============================================================
    // UPDATE TEST SUITE
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<TestSuites> updateTestSuite(@PathVariable Long id,
                                                      @RequestBody TestSuites testSuite,
                                                      Authentication authentication) {
        Long orgId = getOrgId(authentication);

        testSuite.setId(id);

        TestSuites updated =
                testSuitesService.saveTestSuite(orgId,
                        testSuite);

        return ResponseEntity.ok(updated);
    }


    // ============================================================
    // DELETE TEST SUITE
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestSuite(@PathVariable Long id,
                                                Authentication authentication) {
        Long orgId = getOrgId(authentication);

        testSuitesService.deleteTestSuite(orgId,
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

