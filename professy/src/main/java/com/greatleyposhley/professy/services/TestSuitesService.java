package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.TestSuites;
import com.greatleyposhley.professy.repositories.TestSuitesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestSuitesService {

    private final TestSuitesRepository testSuitesRepository;

    @Autowired
    public TestSuitesService(
            TestSuitesRepository testSuitesRepository
    ) {
        this.testSuitesRepository = testSuitesRepository;
    }


    // ============================================================
    // GET ALL
    // ============================================================

    public List<TestSuites> getAllTestSuites() {

        return testSuitesRepository.findAll();
    }


    // ============================================================
    // GET BY ID
    // ============================================================

    public TestSuites getTestSuiteById(Long id) {

        return testSuitesRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Test Suite not found with id: " + id
                        )
                );
    }


    // ============================================================
    // GET BY TEST PLAN
    // ============================================================

    public List<TestSuites> getTestSuitesByTestPlanId(
            Long testPlanId
    ) {

        return testSuitesRepository.findByTestPlanId(testPlanId);
    }


    // ============================================================
    // GET BY PROJECT
    // ============================================================

    public List<TestSuites> getTestSuitesByProjectId(
            Long projectId
    ) {

        return testSuitesRepository.findByProjectId(projectId);
    }


    // ============================================================
    // CREATE / UPDATE
    // ============================================================

    public TestSuites saveTestSuite(TestSuites testSuite) {

        return testSuitesRepository.save(testSuite);
    }


    // ============================================================
    // DELETE
    // ============================================================

    public void deleteTestSuite(Long id) {

        if (!testSuitesRepository.existsById(id)) {

            throw new RuntimeException(
                    "Test Suite not found with id: " + id
            );
        }

        testSuitesRepository.deleteById(id);
    }
}

