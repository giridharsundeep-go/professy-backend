package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.Testcases;
import com.greatleyposhley.professy.services.TestcasesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/testcases")
@CrossOrigin(origins = "*")
public class TestcasesController {

    private final TestcasesService testcasesService;

    @Autowired
    public TestcasesController(TestcasesService testcasesService) {
        this.testcasesService = testcasesService;
    }

    @GetMapping
    public ResponseEntity<List<Testcases>> getAllTestcases(@RequestParam(required = false) Long projectId) {
        if (projectId != null) {
            return ResponseEntity.ok(testcasesService.getTestcasesByProjectId(projectId));
        }
        return ResponseEntity.ok(testcasesService.getAllTestcases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Testcases> getTestcaseById(@PathVariable Long id) {
        return ResponseEntity.ok(testcasesService.getTestcaseById(id));
    }

    @PostMapping
    public ResponseEntity<Testcases> createTestcase(@RequestBody Testcases testcase) {
        Testcases created = testcasesService.saveTestcase(testcase);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Testcases> updateTestCase(@PathVariable Long id, @RequestBody Testcases testcase) {
        testcase.setId(id);
        return ResponseEntity.ok(testcasesService.saveTestcase(testcase));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestcase(@PathVariable Long id) {
        testcasesService.deleteTestcase(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{testCaseId}/assign")
    public ResponseEntity<Testcases> assignTestCase(
            @PathVariable Long testCaseId,
            @RequestBody Map<String, Object> request
    ) {

        String type = String.valueOf(request.get("type"));
        Long itemId = Long.valueOf(
                String.valueOf(request.get("id"))
        );

        Testcases testcase =
                testcasesService.assignTestCase(
                        testCaseId,
                        type,
                        itemId
                );

        return ResponseEntity.ok(testcase);
    }

    @PutMapping("/{testCaseId}/unassign")
    public ResponseEntity<Testcases> unassignTestCase(
            @PathVariable Long testCaseId,
            @RequestBody Map<String, Object> request
    ) {

        String type = String.valueOf(request.get("type"));
        Long itemId = Long.valueOf(
                String.valueOf(request.get("id"))
        );

        Testcases testcase =
                testcasesService.unassignTestCase(
                        testCaseId,
                        type,
                        itemId
                );

        return ResponseEntity.ok(testcase);
    }

}
