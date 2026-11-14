package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
public class TestcasesService {

    private final TestcasesRepository testcasesRepository;
    private final ProjectsRepository projectsRepository;
    private final EpicsRepository epicsRepository;
    private final StoriesRepository storiesRepository;
    private final TasksRepository tasksRepository;
    private final UserAccountRepository usersRepository;
    private final TestSuitesRepository testSuitesRepository;
    private final IssuesRepository issuesRepository;


    @Autowired
    public TestcasesService(TestcasesRepository testcasesRepository,
                            ProjectsRepository projectsRepository,
                            EpicsRepository epicsRepository,
                            StoriesRepository storiesRepository,
                            TasksRepository tasksRepository,
                            UserAccountRepository usersRepository,
                            TestSuitesRepository testSuitesRepository,
                            IssuesRepository issuesRepository) {
        this.testcasesRepository = testcasesRepository;
        this.projectsRepository = projectsRepository;
        this.epicsRepository = epicsRepository;
        this.storiesRepository = storiesRepository;
        this.tasksRepository = tasksRepository;
        this.usersRepository = usersRepository;
        this.testSuitesRepository = testSuitesRepository;
        this.issuesRepository = issuesRepository;
    }

    @Transactional(readOnly = true)
    public List<Testcases> getAllTestcases(Long orgId) {
        return testcasesRepository.findAllByOrgId(orgId);
    }

    @Transactional(readOnly = true)
    public List<Testcases> getTestcasesByProjectId(Long orgId, Long projectId) {
        return testcasesRepository.findByOrgIdAndProjectId(orgId, projectId);
    }

    @Transactional(readOnly = true)
    public Testcases getTestcaseById(Long orgId, Long id) {
        return testcasesRepository.findByIdAndOrgId(orgId, id)
                .orElseThrow(() -> new EntityNotFoundException("Test case not found with ID: " + id));
    }

    @Transactional
    public Testcases saveTestcase(Long orgId, Testcases testcase) {
        testcase.setOrgId(orgId);
        if (testcase.isTagsProvided()) testcase.setTags(normalizeTags(testcase.getTags()));

        /*
         * ============================================================
         * UPDATE EXISTING TEST CASE
         * ============================================================
         */
        if (testcase.getId() != null) {

            Testcases existing = testcasesRepository
                    .findByIdAndOrgId(testcase.getId(), orgId)
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
            if (testcase.isTagsProvided()) existing.setTags(testcase.getTags());
            if (testcase.getPreconditions() != null) existing.setPreconditions(testcase.getPreconditions());
            if (testcase.getSteps() != null) existing.setSteps(testcase.getSteps());
            if (testcase.getActualResult() != null) existing.setActualResult(testcase.getActualResult());
            if (testcase.getPriority() != null) existing.setPriority(testcase.getPriority());
            if (testcase.getTestCaseCode() != null) existing.setTestCaseCode(testcase.getTestCaseCode());
            if (testcase.getType() != null) existing.setType(testcase.getType());

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

                existing.setTestSuite(
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
            if(testcase.isUserProvided() && testcase.getUser()==null)existing.setUser(null);
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
    public Optional<Testcases> updateTestCase(Long orgId, Long id, Testcases updatedTestCase) {
        return testcasesRepository.findByIdAndOrgId(orgId, id).map(existingTestCase -> {

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
                UserAccount user = usersRepository.findById(updatedTestCase.getUser().getId()).orElse(null);
                existingTestCase.setUser(user);
            } else {
                existingTestCase.setUser(null);
            }

            // Creator (Optional)
            if (updatedTestCase.getCreator() != null && updatedTestCase.getCreator().getId() != null && updatedTestCase.getCreator().getId() > 0) {
                UserAccount creator = usersRepository.findById(updatedTestCase.getCreator().getId()).orElse(null);
                existingTestCase.setCreator(creator);
            } else {
                existingTestCase.setCreator(null);
            }

            return testcasesRepository.save(existingTestCase);
        });
    }

    @Transactional
    public void deleteTestcase(Long orgId, Long id) {
        if (!testcasesRepository.existsById(id)) {
            throw new EntityNotFoundException("Test case not found with ID: " + id);
        }
        testcasesRepository.deleteById(id);
    }


    @Transactional(readOnly = true)
    public List<Testcases> filterByTags(Long orgId, Long projectId, Collection<String> tags) {
        Set<String> normalized = normalizeTags(tags);
        if (normalized.isEmpty()) return projectId == null ? getAllTestcases(orgId) : getTestcasesByProjectId(orgId, projectId);
        return testcasesRepository.findByTags(orgId, projectId, List.copyOf(normalized));
    }

    @Transactional(readOnly = true)
    public List<String> getTags(Long orgId, Long projectId) {
        return testcasesRepository.findTags(orgId, projectId);
    }

    @Transactional
    public Testcases updateTags(Long orgId, Long id, Collection<String> tags) {
        Testcases testcase = getTestcaseById(orgId, id);
        testcase.setTags(normalizeTags(tags));
        return testcasesRepository.save(testcase);
    }

    static Set<String> normalizeTags(Collection<String> values) {
        Set<String> tags = new LinkedHashSet<>();
        if (values == null) return tags;
        for (String value : values) {
            if (value == null) continue;
            String tag = value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
            if (tag.isEmpty()) continue;
            if (tag.length() > 40) throw new IllegalArgumentException("Tags must contain at most 40 characters");
            tags.add(tag);
        }
        if (tags.size() > 20) throw new IllegalArgumentException("A test case can have at most 20 tags");
        return tags;
    }

    @Transactional
    public Testcases assignTestCase(Long orgId, Long testCaseId, String type, Long itemId) {
        Testcases testcase = getTestcaseById(orgId, testCaseId);
        if (itemId == null || itemId <= 0) throw new IllegalArgumentException("A valid work item is required");
        switch (type.toUpperCase(Locale.ROOT)) {
            case "EPIC":
                Epics epic = epicsRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Epic not found"));
                requireSameProject(testcase, epic.getProject(), orgId);
                clearAllocation(testcase);
                testcase.setEpic(epic);
                break;
            case "STORY":
                Stories story = storiesRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Story not found"));
                requireSameProject(testcase, story.getProject(), orgId);
                clearAllocation(testcase);
                testcase.setStory(story);
                break;
            case "TASK":
                Tasks task = tasksRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Task not found"));
                requireSameProject(testcase, task.getStory() == null ? null : task.getStory().getProject(), orgId);
                clearAllocation(testcase);
                testcase.setTask(task);
                break;
            case "ISSUE":
                Issues issue = issuesRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Issue not found"));
                requireSameProject(testcase, issue.getProject(), orgId);
                clearAllocation(testcase);
                testcase.setIssue(issue);
                break;
            default: throw new IllegalArgumentException("Unsupported test case assignment type: " + type);
        }
        return testcasesRepository.save(testcase);
    }

    @Transactional
    public Testcases unassignTestCase(Long orgId, Long testCaseId, String type, Long itemId) {
        Testcases testcase = getTestcaseById(orgId, testCaseId);
        switch (type.toUpperCase(Locale.ROOT)) {
            case "EPIC":
                if (testcase.getEpic() != null && Objects.equals(testcase.getEpic().getId(), itemId)) testcase.setEpic(null);
                break;
            case "STORY":
                if (testcase.getStory() != null && Objects.equals(testcase.getStory().getId(), itemId)) testcase.setStory(null);
                break;
            case "TASK":
                if (testcase.getTask() != null && Objects.equals(testcase.getTask().getId(), itemId)) testcase.setTask(null);
                break;
            case "ISSUE":
                if (testcase.getIssue() != null && Objects.equals(testcase.getIssue().getId(), itemId)) testcase.setIssue(null);
                break;
            default: throw new IllegalArgumentException("Unsupported test case assignment type: " + type);
        }
        return testcasesRepository.save(testcase);
    }

    private void requireSameProject(Testcases testcase, Projects project, Long orgId) {
        if (project == null || testcase.getProject() == null || !Objects.equals(project.getOrgId(), orgId)
                || !Objects.equals(testcase.getProject().getId(), project.getId())) {
            throw new IllegalArgumentException("Choose a work item from this test case's project");
        }
    }

    private void clearAllocation(Testcases testcase) {
        testcase.setEpic(null);
        testcase.setStory(null);
        testcase.setTask(null);
        testcase.setIssue(null);
    }
}
