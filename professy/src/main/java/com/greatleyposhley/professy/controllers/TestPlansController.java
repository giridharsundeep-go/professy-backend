package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.TestPlans;
import com.greatleyposhley.professy.services.TestPlansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-plans")
@CrossOrigin(origins = "*")
public class TestPlansController {

    private final TestPlansService testPlansService;

    @Autowired
    public TestPlansController(TestPlansService testPlansService) {
        this.testPlansService = testPlansService;
    }


    // ============================================================
    // GET ALL TEST PLANS
    // ============================================================

    @GetMapping
    public ResponseEntity<List<TestPlans>> getAllTestPlans() {

        return ResponseEntity.ok(
                testPlansService.getAllTestPlans()
        );
    }


    // ============================================================
    // GET TEST PLAN BY ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<TestPlans> getTestPlanById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                testPlansService.getTestPlanById(id)
        );
    }


    // ============================================================
    // GET TEST PLANS BY PROJECT
    // ============================================================

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TestPlans>> getTestPlansByProjectId(
            @PathVariable Long projectId
    ) {

        return ResponseEntity.ok(
                testPlansService.getTestPlansByProjectId(projectId)
        );
    }


    // ============================================================
    // CREATE TEST PLAN
    // ============================================================

    @PostMapping
    public ResponseEntity<TestPlans> createTestPlan(
            @RequestBody TestPlans testPlan
    ) {

        TestPlans saved =
                testPlansService.saveTestPlan(testPlan);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }


    // ============================================================
    // UPDATE TEST PLAN
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<TestPlans> updateTestPlan(
            @PathVariable Long id,
            @RequestBody TestPlans testPlan
    ) {

        testPlan.setId(id);

        TestPlans updated =
                testPlansService.saveTestPlan(testPlan);

        return ResponseEntity.ok(updated);
    }


    // ============================================================
    // DELETE TEST PLAN
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestPlan(
            @PathVariable Long id
    ) {

        testPlansService.deleteTestPlan(id);

        return ResponseEntity.noContent().build();
    }
}

