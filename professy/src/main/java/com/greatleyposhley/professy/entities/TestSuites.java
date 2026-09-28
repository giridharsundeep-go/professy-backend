package com.greatleyposhley.professy.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "test_suites")
@JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
})
public class TestSuites {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnoreProperties({
            "testPlans",
            "testSuites",
            "testcases",
            "product",
            "userAccount",
            "hibernateLazyInitializer",
            "handler"
    })
    private Projects project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_plan_id", nullable = false)
    @JsonIgnoreProperties({
            "project",
            "testSuites",
            "hibernateLazyInitializer",
            "handler"
    })
    private TestPlans testPlan;


    public TestSuites() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Projects getProject() {
        return project;
    }

    public void setProject(Projects project) {
        this.project = project;
    }


    public TestPlans getTestPlan() {
        return testPlan;
    }

    public void setTestPlan(TestPlans testPlan) {
        this.testPlan = testPlan;
    }
}