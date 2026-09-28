package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TestcasesService {

    private final TestcasesRepository testcasesRepository;
    private final ProjectsRepository projectsRepository;
    private final EpicsRepository epicsRepository;
    private final StoriesRepository storiesRepository;
    private final TasksRepository tasksRepository;
    private final UsersRepository usersRepository;
    private final TestSuitesRepository testSuitesRepository;


    @Autowired
    public TestcasesService(TestcasesRepository testcasesRepository,
                            ProjectsRepository projectsRepository,
                            EpicsRepository epicsRepository,
                            StoriesRepository storiesRepository,
                            TasksRepository tasksRepository,
                            UsersRepository usersRepository,
                            TestSuitesRepository testSuitesRepository) {
        this.testcasesRepository = testcasesRepository;
        this.projectsRepository = projectsRepository;
        this.epicsRepository = epicsRepository;
        this.storiesRepository = storiesRepository;
        this.tasksRepository = tasksRepository;
        this.usersRepository = usersRepository;
        this.testSuitesRepository = testSuitesRepository;
    }

    @Transactional(readOnly = true)
    public List<Testcases> getAllTestcases() {
        return testcasesRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Testcases> getTestcasesByProjectId(Long projectId) {
        return testcasesRepository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public Testcases getTestcaseById(Long id) {
        return testcasesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test case not found with ID: " + id));
    }

    @Transactional
    public Testcases saveTestcase(Testcases testcase) {

        /*
         * ============================================================
         * UPDATE EXISTING TEST CASE
         * ============================================================
         */
        if (testcase.getId() != null) {

            Testcases existing = testcasesRepository
                    .findById(testcase.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Test case not found: " + testcase.getId()
                            )
                    );

            /*
             * --------------------------------------------------------
             * Basic fields
             * --------------------------------------------------------
             */
            if (testcase.getTitle() != null) {
                existing.setTitle(testcase.getTitle());
            }

            if (testcase.getDescription() != null) {
                existing.setDescription(testcase.getDescription());
            }

            if (testcase.getExpectedResult() != null) {
                existing.setExpectedResult(testcase.getExpectedResult());
            }

            if (testcase.getStatus() != null) {
                existing.setStatus(testcase.getStatus());
            }

            if (testcase.getStatus() != null) {
                existing.setStatus(testcase.getStatus());
            }


            /*
             * --------------------------------------------------------
             * Project
             * --------------------------------------------------------
             */
            if (testcase.getProject() != null
                    && testcase.getProject().getId() != null) {

                existing.setProject(
                        projectsRepository
                                .findById(testcase.getProject().getId())
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Project not found: "
                                                        + testcase.getProject().getId()
                                        )
                                )
                );
            }


            /*
             * --------------------------------------------------------
             * Epic
             * --------------------------------------------------------
             */
            if (testcase.getEpic() != null
                    && testcase.getEpic().getId() != null) {

                existing.setEpic(
                        epicsRepository
                                .findById(testcase.getEpic().getId())
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Epic not found: "
                                                        + testcase.getEpic().getId()
                                        )
                                )
                );
            }

            if (testcase.getTestSuite() != null
                    && testcase.getTestSuite().getId() != null) {

                testcase.setTestSuite(
                        testSuitesRepository.findById(
                                testcase.getTestSuite().getId()
                        ).orElseThrow(() ->
                                new RuntimeException(
                                        "Test Suite not found: "
                                                + testcase.getTestSuite().getId()
                                )
                        )
                );
            }


            /*
             * --------------------------------------------------------
             * Story
             * --------------------------------------------------------
             */
            if (testcase.getStory() != null
                    && testcase.getStory().getId() != null) {

                existing.setStory(
                        storiesRepository
                                .findById(testcase.getStory().getId())
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Story not found: "
                                                        + testcase.getStory().getId()
                                        )
                                )
                );
            }


            /*
             * --------------------------------------------------------
             * Task
             * --------------------------------------------------------
             */
            if (testcase.getTask() != null
                    && testcase.getTask().getId() != null) {

                existing.setTask(
                        tasksRepository
                                .findById(testcase.getTask().getId())
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Task not found: "
                                                        + testcase.getTask().getId()
                                        )
                                )
                );
            }


            /*
             * --------------------------------------------------------
             * User
             * --------------------------------------------------------
             */
            if (testcase.getUser() != null
                    && testcase.getUser().getId() != null) {

                existing.setUser(
                        usersRepository
                                .findById(testcase.getUser().getId())
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "User not found: "
                                                        + testcase.getUser().getId()
                                        )
                                )
                );
            }


            /*
             * --------------------------------------------------------
             * Creator
             * --------------------------------------------------------
             */
            if (testcase.getCreator() != null
                    && testcase.getCreator().getId() != null) {

                existing.setCreator(
                        usersRepository
                                .findById(testcase.getCreator().getId())
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Creator not found: "
                                                        + testcase.getCreator().getId()
                                        )
                                )
                );
            }


            /*
             * --------------------------------------------------------
             * Save existing entity
             * --------------------------------------------------------
             */
            return testcasesRepository.save(existing);
        }


        /*
         * ============================================================
         * CREATE NEW TEST CASE
         * ============================================================
         */

        if (testcase.getExpectedResult() == null
                || testcase.getExpectedResult().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Expected result is required when creating a test case"
            );
        }


        /*
         * ------------------------------------------------------------
         * Resolve Project
         * ------------------------------------------------------------
         */
        if (testcase.getProject() != null
                && testcase.getProject().getId() != null) {

            testcase.setProject(
                    projectsRepository
                            .findById(testcase.getProject().getId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Project not found: "
                                                    + testcase.getProject().getId()
                                    )
                            )
            );
        }


        /*
         * ------------------------------------------------------------
         * Resolve Epic
         * ------------------------------------------------------------
         */
        if (testcase.getEpic() != null
                && testcase.getEpic().getId() != null) {

            testcase.setEpic(
                    epicsRepository
                            .findById(testcase.getEpic().getId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Epic not found: "
                                                    + testcase.getEpic().getId()
                                    )
                            )
            );
        }


        /*
         * ------------------------------------------------------------
         * Resolve Story
         * ------------------------------------------------------------
         */
        if (testcase.getStory() != null
                && testcase.getStory().getId() != null) {

            testcase.setStory(
                    storiesRepository
                            .findById(testcase.getStory().getId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Story not found: "
                                                    + testcase.getStory().getId()
                                    )
                            ));
        }


        /*
         * ------------------------------------------------------------
         * Resolve Task
         * ------------------------------------------------------------
         */
        if (testcase.getTask() != null
                && testcase.getTask().getId() != null) {

            testcase.setTask(
                    tasksRepository
                            .findById(testcase.getTask().getId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Task not found: "
                                                    + testcase.getTask().getId()
                                    )
                            ));
        }


        /*
         * ------------------------------------------------------------
         * Resolve User
         * ------------------------------------------------------------
         */
        if (testcase.getUser() != null
                && testcase.getUser().getId() != null) {

            testcase.setUser(
                    usersRepository
                            .findById(testcase.getUser().getId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found: "
                                                    + testcase.getUser().getId()
                                    )
                            ));
        }


        /*
         * ------------------------------------------------------------
         * Resolve Creator
         * ------------------------------------------------------------
         */
        if (testcase.getCreator() != null
                && testcase.getCreator().getId() != null) {

            testcase.setCreator(
                    usersRepository
                            .findById(testcase.getCreator().getId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Creator not found: "
                                                    + testcase.getCreator().getId()
                                    )
                            ));
        }


        return testcasesRepository.save(testcase);
    }

    @Transactional
    public Optional<Testcases> updateTestCase(Long id, Testcases updatedTestCase) {
        return testcasesRepository.findById(id).map(existingTestCase -> {

            // Update basic text & enum fields
            existingTestCase.setTestCaseCode(updatedTestCase.getTestCaseCode());
            existingTestCase.setTitle(updatedTestCase.getTitle());
            existingTestCase.setDescription(updatedTestCase.getDescription());
            existingTestCase.setPreconditions(updatedTestCase.getPreconditions());
            existingTestCase.setSteps(updatedTestCase.getSteps());
            existingTestCase.setExpectedResult(updatedTestCase.getExpectedResult());
            existingTestCase.setActualResult(updatedTestCase.getActualResult());
            existingTestCase.setStatus(updatedTestCase.getStatus());
            existingTestCase.setPriority(updatedTestCase.getPriority());

            // Project (Required - update only if non-null ID provided)
            if (updatedTestCase.getProject() != null && updatedTestCase.getProject().getId() != null) {
                Projects project = projectsRepository.findById(updatedTestCase.getProject().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID: " + updatedTestCase.getProject().getId()));
                existingTestCase.setProject(project);
            }

            // Epic (Optional)
            if (updatedTestCase.getEpic() != null && updatedTestCase.getEpic().getId() != null && updatedTestCase.getEpic().getId() > 0) {
                Epics epic = epicsRepository.findById(updatedTestCase.getEpic().getId()).orElse(null);
                existingTestCase.setEpic(epic);
            } else {
                existingTestCase.setEpic(null);
            }

            // Story (Optional)
            if (updatedTestCase.getStory() != null && updatedTestCase.getStory().getId() != null && updatedTestCase.getStory().getId() > 0) {
                Stories story = storiesRepository.findById(updatedTestCase.getStory().getId()).orElse(null);
                existingTestCase.setStory(story);
            } else {
                existingTestCase.setStory(null);
            }

            // Task (Optional)
            if (updatedTestCase.getTask() != null && updatedTestCase.getTask().getId() != null && updatedTestCase.getTask().getId() > 0) {
                Tasks task = tasksRepository.findById(updatedTestCase.getTask().getId()).orElse(null);
                existingTestCase.setTask(task);
            } else {
                existingTestCase.setTask(null);
            }

            // User / Assignee (Optional)
            if (updatedTestCase.getUser() != null && updatedTestCase.getUser().getId() != null && updatedTestCase.getUser().getId() > 0) {
                Users user = usersRepository.findById(updatedTestCase.getUser().getId()).orElse(null);
                existingTestCase.setUser(user);
            } else {
                existingTestCase.setUser(null);
            }

            // Creator (Optional)
            if (updatedTestCase.getCreator() != null && updatedTestCase.getCreator().getId() != null && updatedTestCase.getCreator().getId() > 0) {
                Users creator = usersRepository.findById(updatedTestCase.getCreator().getId()).orElse(null);
                existingTestCase.setCreator(creator);
            } else {
                existingTestCase.setCreator(null);
            }

            return testcasesRepository.save(existingTestCase);
        });
    }

    @Transactional
    public void deleteTestcase(Long id) {
        if (!testcasesRepository.existsById(id)) {
            throw new EntityNotFoundException("Test case not found with ID: " + id);
        }
        testcasesRepository.deleteById(id);
    }

    @Transactional
    public Testcases assignTestCase(
            Long testCaseId,
            String type,
            Long itemId
    ) {

        Testcases testcase = testcasesRepository
                .findById(testCaseId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Test case not found: " + testCaseId
                        )
                );

        switch (type.toUpperCase()) {

            case "STORY":

                testcase.setStory(
                        storiesRepository
                                .findById(itemId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Story not found: " + itemId
                                        )
                                )
                );

                // A testcase should not simultaneously belong to another level.
                testcase.setTask(null);
                testcase.setEpic(null);

                break;


            case "TASK":

                testcase.setTask(
                        tasksRepository
                                .findById(itemId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Task not found: " + itemId
                                        )
                                )
                );

                testcase.setStory(null);
                testcase.setEpic(null);

                break;


            default:
                throw new IllegalArgumentException(
                        "Unsupported test case assignment type: " + type
                );
        }

        return testcasesRepository.save(testcase);
    }

    @Transactional
    public Testcases unassignTestCase(
            Long testCaseId,
            String type,
            Long itemId
    ) {

        Testcases testcase = testcasesRepository
                .findById(testCaseId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Test case not found: " + testCaseId
                        )
                );

        switch (type.toUpperCase()) {

            case "STORY":

                if (testcase.getStory() != null
                        && testcase.getStory().getId().equals(itemId)) {

                    testcase.setStory(null);
                }

                break;


            case "TASK":

                if (testcase.getTask() != null
                        && testcase.getTask().getId().equals(itemId)) {

                    testcase.setTask(null);
                }

                break;


            default:
                throw new IllegalArgumentException(
                        "Unsupported test case assignment type: " + type
                );
        }

        return testcasesRepository.save(testcase);
    }

}