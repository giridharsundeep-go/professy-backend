package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.TestPlans;
import com.greatleyposhley.professy.repositories.TestPlansRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestPlansService {

    private final TestPlansRepository testPlansRepository;

    @Autowired
    public TestPlansService(
            TestPlansRepository testPlansRepository
    ) {
        this.testPlansRepository = testPlansRepository;
    }


    // ============================================================
    // GET ALL
    // ============================================================

    public List<TestPlans> getAllTestPlans() {
        return testPlansRepository.findAll();
    }


    // ============================================================
    // GET BY ID
    // ============================================================

    public TestPlans getTestPlanById(Long id) {

        return testPlansRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Test Plan not found with id: " + id
                        )
                );
    }


    // ============================================================
    // GET BY PROJECT
    // ============================================================

    public List<TestPlans> getTestPlansByProjectId(
            Long projectId
    ) {

        return testPlansRepository.findByProjectId(projectId);
    }


    // ============================================================
    // CREATE / UPDATE
    // ============================================================

    public TestPlans saveTestPlan(TestPlans testPlan) {

        return testPlansRepository.save(testPlan);
    }


    // ============================================================
    // DELETE
    // ============================================================

    public void deleteTestPlan(Long id) {

        if (!testPlansRepository.existsById(id)) {

            throw new RuntimeException(
                    "Test Plan not found with id: " + id
            );
        }

        testPlansRepository.deleteById(id);
    }
}

