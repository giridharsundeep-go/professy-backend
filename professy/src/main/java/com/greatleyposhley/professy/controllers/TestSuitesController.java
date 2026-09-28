package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.TestSuites;
import com.greatleyposhley.professy.services.TestSuitesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-suites")
@CrossOrigin(origins = "*")
public class TestSuitesController {

    private final TestSuitesService testSuitesService;

    @Autowired
    public TestSuitesController(TestSuitesService testSuitesService) {
        this.testSuitesService = testSuitesService;
    }


    // ============================================================
    // GET ALL TEST SUITES
    // ============================================================

    @GetMapping
    public ResponseEntity<List<TestSuites>> getAllTestSuites() {

        return ResponseEntity.ok(
                testSuitesService.getAllTestSuites()
        );
    }


    // ============================================================
    // GET TEST SUITE BY ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<TestSuites> getTestSuiteById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                testSuitesService.getTestSuiteById(id)
        );
    }


    // ============================================================
    // GET TEST SUITES BY TEST PLAN
    // ============================================================

    @GetMapping("/test-plan/{testPlanId}")
    public ResponseEntity<List<TestSuites>> getTestSuitesByTestPlanId(
            @PathVariable Long testPlanId
    ) {

        return ResponseEntity.ok(
                testSuitesService.getTestSuitesByTestPlanId(testPlanId)
        );
    }


    // ============================================================
    // GET TEST SUITES BY PROJECT
    // ============================================================

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TestSuites>> getTestSuitesByProjectId(
            @PathVariable Long projectId
    ) {

        return ResponseEntity.ok(
                testSuitesService.getTestSuitesByProjectId(projectId)
        );
    }


    // ============================================================
    // CREATE TEST SUITE
    // ============================================================

    @PostMapping
    public ResponseEntity<TestSuites> createTestSuite(
            @RequestBody TestSuites testSuite
    ) {

        TestSuites saved =
                testSuitesService.saveTestSuite(testSuite);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }


    // ============================================================
    // UPDATE TEST SUITE
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<TestSuites> updateTestSuite(
            @PathVariable Long id,
            @RequestBody TestSuites testSuite
    ) {

        testSuite.setId(id);

        TestSuites updated =
                testSuitesService.saveTestSuite(testSuite);

        return ResponseEntity.ok(updated);
    }


    // ============================================================
    // DELETE TEST SUITE
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestSuite(
            @PathVariable Long id
    ) {

        testSuitesService.deleteTestSuite(id);

        return ResponseEntity.noContent().build();
    }
}

