DELIMITER $$
CREATE TRIGGER workspace_epics_insert AFTER INSERT ON epics FOR EACH ROW
BEGIN
CALL workspace_record_ticket_change(NEW.org_id,NEW.project_id,'EPIC',NEW.id,COALESCE(@professy_actor_id,0),'CREATED',NULL,JSON_OBJECT('title',NEW.name,'description',NEW.description,'status',NEW.status,'assigneeId',NEW.assignee_user_id,'reporterId',NEW.reporter_user_id,'creatorId',NEW.creator_user_id,'sprintId',NEW.sprint_id,'key',NEW.epic_code));
END$$
CREATE TRIGGER workspace_epics_update AFTER UPDATE ON epics FOR EACH ROW
BEGIN
IF NOT(OLD.name <=> NEW.name) OR NOT(OLD.description <=> NEW.description) OR NOT(OLD.status <=> NEW.status) OR NOT(OLD.assignee_user_id <=> NEW.assignee_user_id) OR NOT(OLD.reporter_user_id <=> NEW.reporter_user_id) OR NOT(OLD.creator_user_id <=> NEW.creator_user_id) OR NOT(OLD.sprint_id <=> NEW.sprint_id) OR NOT(OLD.epic_code <=> NEW.epic_code) THEN
CALL workspace_record_ticket_change(NEW.org_id,NEW.project_id,'EPIC',NEW.id,COALESCE(@professy_actor_id,0),'UPDATED',JSON_OBJECT('title',OLD.name,'description',OLD.description,'status',OLD.status,'assigneeId',OLD.assignee_user_id,'reporterId',OLD.reporter_user_id,'creatorId',OLD.creator_user_id,'sprintId',OLD.sprint_id,'key',OLD.epic_code),JSON_OBJECT('title',NEW.name,'description',NEW.description,'status',NEW.status,'assigneeId',NEW.assignee_user_id,'reporterId',NEW.reporter_user_id,'creatorId',NEW.creator_user_id,'sprintId',NEW.sprint_id,'key',NEW.epic_code));
END IF;
END$$
CREATE TRIGGER workspace_epics_delete AFTER DELETE ON epics FOR EACH ROW
BEGIN
CALL workspace_record_ticket_change(OLD.org_id,OLD.project_id,'EPIC',OLD.id,COALESCE(@professy_actor_id,0),'DELETED',JSON_OBJECT('title',OLD.name,'description',OLD.description,'status',OLD.status,'assigneeId',OLD.assignee_user_id,'reporterId',OLD.reporter_user_id,'creatorId',OLD.creator_user_id,'sprintId',OLD.sprint_id,'key',OLD.epic_code),NULL);
END$$
CREATE TRIGGER workspace_stories_insert AFTER INSERT ON stories FOR EACH ROW
BEGIN
CALL workspace_record_ticket_change(NEW.org_id,NEW.project_id,'STORY',NEW.id,COALESCE(@professy_actor_id,0),'CREATED',NULL,JSON_OBJECT('title',NEW.title,'description',NEW.description,'status',NEW.status,'priority',NEW.priority,'points',NEW.story_points,'assigneeId',NEW.assignee_user_id,'reporterId',NEW.reporter_user_id,'creatorId',NEW.creator_user_id,'sprintId',NEW.sprint_id,'epicId',NEW.epic_id));
END$$
CREATE TRIGGER workspace_stories_update AFTER UPDATE ON stories FOR EACH ROW
BEGIN
IF NOT(OLD.title <=> NEW.title) OR NOT(OLD.description <=> NEW.description) OR NOT(OLD.status <=> NEW.status) OR NOT(OLD.priority <=> NEW.priority) OR NOT(OLD.story_points <=> NEW.story_points) OR NOT(OLD.assignee_user_id <=> NEW.assignee_user_id) OR NOT(OLD.reporter_user_id <=> NEW.reporter_user_id) OR NOT(OLD.creator_user_id <=> NEW.creator_user_id) OR NOT(OLD.sprint_id <=> NEW.sprint_id) OR NOT(OLD.epic_id <=> NEW.epic_id) THEN
CALL workspace_record_ticket_change(NEW.org_id,NEW.project_id,'STORY',NEW.id,COALESCE(@professy_actor_id,0),'UPDATED',JSON_OBJECT('title',OLD.title,'description',OLD.description,'status',OLD.status,'priority',OLD.priority,'points',OLD.story_points,'assigneeId',OLD.assignee_user_id,'reporterId',OLD.reporter_user_id,'creatorId',OLD.creator_user_id,'sprintId',OLD.sprint_id,'epicId',OLD.epic_id),JSON_OBJECT('title',NEW.title,'description',NEW.description,'status',NEW.status,'priority',NEW.priority,'points',NEW.story_points,'assigneeId',NEW.assignee_user_id,'reporterId',NEW.reporter_user_id,'creatorId',NEW.creator_user_id,'sprintId',NEW.sprint_id,'epicId',NEW.epic_id));
END IF;
END$$
CREATE TRIGGER workspace_stories_delete AFTER DELETE ON stories FOR EACH ROW
BEGIN
CALL workspace_record_ticket_change(OLD.org_id,OLD.project_id,'STORY',OLD.id,COALESCE(@professy_actor_id,0),'DELETED',JSON_OBJECT('title',OLD.title,'description',OLD.description,'status',OLD.status,'priority',OLD.priority,'points',OLD.story_points,'assigneeId',OLD.assignee_user_id,'reporterId',OLD.reporter_user_id,'creatorId',OLD.creator_user_id,'sprintId',OLD.sprint_id,'epicId',OLD.epic_id),NULL);
END$$
CREATE TRIGGER workspace_tasks_insert AFTER INSERT ON tasks FOR EACH ROW
BEGIN
CALL workspace_record_ticket_change(NEW.org_id,(SELECT s.project_id FROM stories s WHERE s.id=NEW.story_id),'TASK',NEW.id,COALESCE(@professy_actor_id,0),'CREATED',NULL,JSON_OBJECT('title',NEW.title,'description',NEW.description,'status',NEW.status,'assigneeId',NEW.assignee_user_id,'reporterId',NEW.reporter_user_id,'creatorId',NEW.creator_user_id,'sprintId',NEW.sprint_id,'storyId',NEW.story_id));
END$$
CREATE TRIGGER workspace_tasks_update AFTER UPDATE ON tasks FOR EACH ROW
BEGIN
IF NOT(OLD.title <=> NEW.title) OR NOT(OLD.description <=> NEW.description) OR NOT(OLD.status <=> NEW.status) OR NOT(OLD.assignee_user_id <=> NEW.assignee_user_id) OR NOT(OLD.reporter_user_id <=> NEW.reporter_user_id) OR NOT(OLD.creator_user_id <=> NEW.creator_user_id) OR NOT(OLD.sprint_id <=> NEW.sprint_id) OR NOT(OLD.story_id <=> NEW.story_id) THEN
CALL workspace_record_ticket_change(NEW.org_id,(SELECT s.project_id FROM stories s WHERE s.id=NEW.story_id),'TASK',NEW.id,COALESCE(@professy_actor_id,0),'UPDATED',JSON_OBJECT('title',OLD.title,'description',OLD.description,'status',OLD.status,'assigneeId',OLD.assignee_user_id,'reporterId',OLD.reporter_user_id,'creatorId',OLD.creator_user_id,'sprintId',OLD.sprint_id,'storyId',OLD.story_id),JSON_OBJECT('title',NEW.title,'description',NEW.description,'status',NEW.status,'assigneeId',NEW.assignee_user_id,'reporterId',NEW.reporter_user_id,'creatorId',NEW.creator_user_id,'sprintId',NEW.sprint_id,'storyId',NEW.story_id));
END IF;
END$$
CREATE TRIGGER workspace_tasks_delete AFTER DELETE ON tasks FOR EACH ROW
BEGIN
CALL workspace_record_ticket_change(OLD.org_id,(SELECT s.project_id FROM stories s WHERE s.id=OLD.story_id),'TASK',OLD.id,COALESCE(@professy_actor_id,0),'DELETED',JSON_OBJECT('title',OLD.title,'description',OLD.description,'status',OLD.status,'assigneeId',OLD.assignee_user_id,'reporterId',OLD.reporter_user_id,'creatorId',OLD.creator_user_id,'sprintId',OLD.sprint_id,'storyId',OLD.story_id),NULL);
END$$
CREATE TRIGGER workspace_issues_insert AFTER INSERT ON issues FOR EACH ROW
BEGIN
CALL workspace_record_ticket_change(NEW.org_id,NEW.project_id,'ISSUE',NEW.id,COALESCE(@professy_actor_id,0),'CREATED',NULL,JSON_OBJECT('title',NEW.title,'description',NEW.description,'status',NEW.status,'assigneeId',NEW.assignee_user_id,'reporterId',NEW.reporter_user_id,'creatorId',NEW.creator_user_id,'sprintId',NEW.sprint_id,'key',NEW.issue_code));
END$$
CREATE TRIGGER workspace_issues_update AFTER UPDATE ON issues FOR EACH ROW
BEGIN
IF NOT(OLD.title <=> NEW.title) OR NOT(OLD.description <=> NEW.description) OR NOT(OLD.status <=> NEW.status) OR NOT(OLD.assignee_user_id <=> NEW.assignee_user_id) OR NOT(OLD.reporter_user_id <=> NEW.reporter_user_id) OR NOT(OLD.creator_user_id <=> NEW.creator_user_id) OR NOT(OLD.sprint_id <=> NEW.sprint_id) OR NOT(OLD.issue_code <=> NEW.issue_code) THEN
CALL workspace_record_ticket_change(NEW.org_id,NEW.project_id,'ISSUE',NEW.id,COALESCE(@professy_actor_id,0),'UPDATED',JSON_OBJECT('title',OLD.title,'description',OLD.description,'status',OLD.status,'assigneeId',OLD.assignee_user_id,'reporterId',OLD.reporter_user_id,'creatorId',OLD.creator_user_id,'sprintId',OLD.sprint_id,'key',OLD.issue_code),JSON_OBJECT('title',NEW.title,'description',NEW.description,'status',NEW.status,'assigneeId',NEW.assignee_user_id,'reporterId',NEW.reporter_user_id,'creatorId',NEW.creator_user_id,'sprintId',NEW.sprint_id,'key',NEW.issue_code));
END IF;
END$$
CREATE TRIGGER workspace_issues_delete AFTER DELETE ON issues FOR EACH ROW
BEGIN
CALL workspace_record_ticket_change(OLD.org_id,OLD.project_id,'ISSUE',OLD.id,COALESCE(@professy_actor_id,0),'DELETED',JSON_OBJECT('title',OLD.title,'description',OLD.description,'status',OLD.status,'assigneeId',OLD.assignee_user_id,'reporterId',OLD.reporter_user_id,'creatorId',OLD.creator_user_id,'sprintId',OLD.sprint_id,'key',OLD.issue_code),NULL);
END$$
CREATE TRIGGER workspace_testcases_insert AFTER INSERT ON testcases FOR EACH ROW
BEGIN
CALL workspace_record_ticket_change(NEW.org_id,NEW.project_id,'TEST_CASE',NEW.id,COALESCE(@professy_actor_id,0),'CREATED',NULL,JSON_OBJECT('title',NEW.title,'description',NEW.description,'status',NEW.status,'priority',NEW.priority,'steps',NEW.steps,'preconditions',NEW.preconditions,'expectedResult',NEW.expected_result,'actualResult',NEW.actual_result,'type',NEW.test_type,'assigneeId',NEW.user_id,'creatorId',NEW.created_by,'epicId',NEW.epic_id,'storyId',NEW.story_id,'taskId',NEW.task_id,'issueId',NEW.issue_id));
END$$
CREATE TRIGGER workspace_testcases_update AFTER UPDATE ON testcases FOR EACH ROW
BEGIN
IF NOT(OLD.title <=> NEW.title) OR NOT(OLD.description <=> NEW.description) OR NOT(OLD.status <=> NEW.status) OR NOT(OLD.priority <=> NEW.priority) OR NOT(OLD.steps <=> NEW.steps) OR NOT(OLD.preconditions <=> NEW.preconditions) OR NOT(OLD.expected_result <=> NEW.expected_result) OR NOT(OLD.actual_result <=> NEW.actual_result) OR NOT(OLD.test_type <=> NEW.test_type) OR NOT(OLD.user_id <=> NEW.user_id) OR NOT(OLD.created_by <=> NEW.created_by) OR NOT(OLD.epic_id <=> NEW.epic_id) OR NOT(OLD.story_id <=> NEW.story_id) OR NOT(OLD.task_id <=> NEW.task_id) OR NOT(OLD.issue_id <=> NEW.issue_id) THEN
CALL workspace_record_ticket_change(NEW.org_id,NEW.project_id,'TEST_CASE',NEW.id,COALESCE(@professy_actor_id,0),'UPDATED',JSON_OBJECT('title',OLD.title,'description',OLD.description,'status',OLD.status,'priority',OLD.priority,'steps',OLD.steps,'preconditions',OLD.preconditions,'expectedResult',OLD.expected_result,'actualResult',OLD.actual_result,'type',OLD.test_type,'assigneeId',OLD.user_id,'creatorId',OLD.created_by,'epicId',OLD.epic_id,'storyId',OLD.story_id,'taskId',OLD.task_id,'issueId',OLD.issue_id),JSON_OBJECT('title',NEW.title,'description',NEW.description,'status',NEW.status,'priority',NEW.priority,'steps',NEW.steps,'preconditions',NEW.preconditions,'expectedResult',NEW.expected_result,'actualResult',NEW.actual_result,'type',NEW.test_type,'assigneeId',NEW.user_id,'creatorId',NEW.created_by,'epicId',NEW.epic_id,'storyId',NEW.story_id,'taskId',NEW.task_id,'issueId',NEW.issue_id));
END IF;
END$$
CREATE TRIGGER workspace_testcases_delete AFTER DELETE ON testcases FOR EACH ROW
BEGIN
CALL workspace_record_ticket_change(OLD.org_id,OLD.project_id,'TEST_CASE',OLD.id,COALESCE(@professy_actor_id,0),'DELETED',JSON_OBJECT('title',OLD.title,'description',OLD.description,'status',OLD.status,'priority',OLD.priority,'steps',OLD.steps,'preconditions',OLD.preconditions,'expectedResult',OLD.expected_result,'actualResult',OLD.actual_result,'type',OLD.test_type,'assigneeId',OLD.user_id,'creatorId',OLD.created_by,'epicId',OLD.epic_id,'storyId',OLD.story_id,'taskId',OLD.task_id,'issueId',OLD.issue_id),NULL);
END$$
CREATE TRIGGER workspace_testcase_tags_insert AFTER INSERT ON testcase_tags FOR EACH ROW
BEGIN
DECLARE org_value BIGINT; DECLARE project_value BIGINT;
SELECT org_id,project_id INTO org_value,project_value FROM testcases WHERE id=NEW.testcase_id;
CALL workspace_record_ticket_change(org_value,project_value,'TEST_CASE',NEW.testcase_id,COALESCE(@professy_actor_id,0),'TAG_ADDED',NULL,JSON_OBJECT('tag',NEW.tag));
END$$
CREATE TRIGGER workspace_testcase_tags_delete AFTER DELETE ON testcase_tags FOR EACH ROW
BEGIN
DECLARE org_value BIGINT; DECLARE project_value BIGINT;
SELECT org_id,project_id INTO org_value,project_value FROM testcases WHERE id=OLD.testcase_id;
CALL workspace_record_ticket_change(org_value,project_value,'TEST_CASE',OLD.testcase_id,COALESCE(@professy_actor_id,0),'TAG_REMOVED',JSON_OBJECT('tag',OLD.tag),NULL);
END$$
DELIMITER ;
INSERT IGNORE INTO workspace_schema_migrations(version) VALUES ('20261103_ticket_audit');
